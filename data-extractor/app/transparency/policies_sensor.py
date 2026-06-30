
from bs4 import BeautifulSoup
from urllib.parse import urljoin
from playwright.sync_api import Playwright, sync_playwright

from app.transparency.npl_manager import get_nlp



class PolicySensor:


    def __init__(self):
        self.nlp = get_nlp()

    def sense(self, base_url) -> dict:
        with  sync_playwright() as p:
            browser = p.chromium.launch(headless=True)

            context = browser.new_context(user_agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            page = context.new_page()

            page.goto(base_url, timeout=0)

            # Wait for a specific element (like the search bar) to ensure JS rendered
            page.wait_for_selector("body", timeout=0)
        
            # Now you can get the full rendered HTML
            print(page.title())
            html_content = page.content()
        
            policy_links = self.find_policy_links(html_content, base_url) 

    


            results = {}
            for category, policy_url in policy_links.items():
                page.goto(policy_url, wait_until="networkidle", timeout=0)

                #page.wait_for_selector("body", timeout=0)

                page.wait_for_selector("main, article, .content, #main, body", timeout=0)
                html_content = page.content()

                soup = BeautifulSoup(html_content, 'html.parser')
                results[category] = soup.get_text(separator='\n', strip=True)

            #page.goto(policy_url, timeout=0)

            ## Wait for the main content to load (adjust selector as needed)
            #page.wait_for_selector("body", timeout=0)
        
            ## Get the full rendered HTML
            #html_content = page.content()
        
            #browser.close()
        
            #soup = BeautifulSoup(html_content, 'html.parser')
            #return soup.get_text(separator='\n', strip=True)



    
            return results


    def find_policy_links(self, html_content, base_url):
        # Mapping of categories to their keywords
        categories = {  
            "refund": ["refund policy", "return policy", "refund notice", "exchange policy", "cancellation policy"],
            "privacy": ["privacy policy", "data policy", "privacy notice", "disclosure policy", "confidentiality policy"],
            "terms": ["terms and conditions", "terms of use", "conditions of use", "legal agreement", "legal notice", "terms of service"],
        }
    
        soup = BeautifulSoup(html_content, 'html.parser')
        found_urls = {}

        for link in soup.find_all('a', href=True):
            link_text = link.text.lower()
            link_href = link['href'].lower()

            for category, keywords in categories.items():
                if any(kw in link_text or kw in link_href for kw in keywords):
                    # Ensure the link is an absolute URL
                    full_url = urljoin(base_url, link['href'])
                    found_urls[category] = full_url

        return found_urls





    def _get_base_metrics(self, doc):
            """Helper method to extract structural NLP metrics used by all policies."""
            sentences = list(doc.sents)
            num_sentences = len(sentences)
            words = [t for t in doc if not t.is_punct and not t.is_space]
            num_words = len(words)
            
            if num_words == 0 or num_sentences == 0:
                return None
                
            avg_sentence_len = num_words / num_sentences
            passive_count = sum(1 for t in doc if t.dep_ == "auxpass")
            passive_density = (passive_count / num_words) * 100
            
            # Base readability calculation
            readability = max(0, min(100, 100 - (avg_sentence_len - 15) * 3))
            # Base tone clarity calculation (penalizing heavy passive voice)
            tone_clarity = max(0, min(100, 100 - (passive_density * 10)))
            
            # Extract all lemmas for keyword matching
            lemmas = set(t.lemma_.lower() for t in words)
            
            return {
                "num_words": num_words,
                "readability": readability,
                "tone_clarity": tone_clarity,
                "lemmas": lemmas
            }

    def analyze_privacy_policy(self, text: str) -> dict:
        """
        Evaluates Privacy Policies based on data protection compliance (GDPR/CCPA signals).
        Focuses heavily on transparency regarding data collection, usage, and user rights.
        """
        doc = self.nlp(text)
        base = self._get_base_metrics(doc)
        if not base:
            return {"error": "Empty text"}

        # Privacy-specific compliance checklist
        required_terms = {
            "cookie", "collect", "share", "third-party", "opt-out", 
            "security", "encryption", "disclose", "consent", "rights"
        }
        matched_terms = base["lemmas"].intersection(required_terms)
        compliance_score = (len(matched_terms) / len(required_terms)) * 100
        
        # Privacy policies are inherently dense; we weight compliance and clarity highest
        final_score = (base["readability"] * 0.30) + (compliance_score * 0.50) + (base["tone_clarity"] * 0.20)
        
        return {
            "policy_type": "Privacy Policy",
            "quality_score": round(final_score, 1),
            "breakdown": {
                "readability": round(base["readability"], 1),
                "legal_compliance": round(compliance_score, 1),
                "tone_clarity": round(base["tone_clarity"], 1)
            },
            "tracked_provisions_found": list(matched_terms),
            "missing_provisions": list(required_terms - matched_terms)
        }

    def analyze_return_refund_policy(self, text: str) -> dict:
        """
        Evaluates Return and Refund policies based on consumer clarity.
        Looks heavily for actionable timelines, logistics, and financial conditions.
        """
        doc = self.nlp(text)
        base = self._get_base_metrics(doc)
        if not base:
            return {"error": "Empty text"}

        # Return/Refund specific checklist
        required_terms = {
            "return", "refund", "exchange", "days", "shipping", 
            "condition", "fee", "restocking", "credit", "damaged"
        }
        matched_terms = base["lemmas"].intersection(required_terms)
        compliance_score = (len(matched_terms) / len(required_terms)) * 100
        
        # Look for time constraints using spaCy's entity recognition (e.g., "30 days")
        time_entities = [ent.text for ent in doc.ents if ent.label_ == "DATE"]
        has_timeframe = any(any(char.isdigit() for char in ent) for ent in time_entities)
        
        # Bonus or penalty based on explicit timeframe mention
        if not has_timeframe:
            compliance_score = max(0, compliance_score - 20)

        # For consumers, readability and plain tone matter immensely
        final_score = (base["readability"] * 0.40) + (compliance_score * 0.40) + (base["tone_clarity"] * 0.20)
        
        return {
            "policy_type": "Return & Refund Policy",
            "quality_score": round(final_score, 1),
            "breakdown": {
                "readability": round(base["readability"], 1),
                "logistic_clarity": round(compliance_score, 1),
                "tone_clarity": round(base["tone_clarity"], 1)
            },
            "detected_timeframes": time_entities[:5], # Show top 5 detected date contexts
            "tracked_provisions_found": list(matched_terms)
        }

    def analyze_terms_of_use(self, text: str) -> dict:
        """
        Evaluates Terms of Use/Service based on liability protection and rules of conduct.
        """
        doc = self.nlp(text)
        base = self._get_base_metrics(doc)
        if not base:
            return {"error": "Empty text"}

        # Terms of Use specific checklist
        required_terms = {
            "termination", "liability", "governing", "law", "jurisdiction",
            "intellectual", "property", "account", "prohibited", "warranty"
        }
        matched_terms = base["lemmas"].intersection(required_terms)
        compliance_score = (len(matched_terms) / len(required_terms)) * 100
        
        # Terms of use are strictly legal frameworks; compliance to protective clauses is heavily weighted
        final_score = (base["readability"] * 0.20) + (compliance_score * 0.60) + (base["tone_clarity"] * 0.20)
        
        return {
            "policy_type": "Terms of Use",
            "quality_score": round(final_score, 1),
            "breakdown": {
                "readability": round(base["readability"], 1),
                "liability_protection": round(compliance_score, 1),
                "tone_clarity": round(base["tone_clarity"], 1)
            },
            "tracked_provisions_found": list(matched_terms),
            "missing_provisions": list(required_terms - matched_terms)
        }