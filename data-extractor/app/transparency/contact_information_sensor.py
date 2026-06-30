import whois


class ContactInformationSensor:
    def __init__(self):
        # Initialize any necessary resources, e.g., API clients
        pass
    
    def sense(self, domain_name):
        # Placeholder for actual contact information retrieval logic
        # This could involve WHOIS lookups, web scraping, or using third-party APIs
        try:
            w = whois.whois(domain_name)
            name = w.name if hasattr(w, 'name') else None
            org = w.org if hasattr(w, 'org') else None
            address = w.address if hasattr(w, 'address') else None
            city = w.city if hasattr(w, 'city') else None
            state = w.state if hasattr(w, 'state') else None
            country = w.country if hasattr(w, 'country') else None
            postal_code = w.registrant_postal_code if hasattr(w, 'registrant_postal_code') else None

            score = 0
            if name:
                score += 1
            if org:
                score += 1
            if address:
                score += 1
            if city:
                score += 1
            if state:
                score += 1
            if country:
                score += 1
            if postal_code:
                score += 1

            score = score / 7 * 100

            return {
                "name": name,
                "org": org,
                "address": address,
                "city": city,
                "state": state,
                "country": country,
                "postal_code": postal_code,
                "score": score
            }
        except Exception as e:
            return f"Error: {str(e)}"