import requests

from flask import current_app


class SocialMediaPresenceSensor:

    url: str

    def __init__(self):
        self.api_key = current_app.config.get('X_API_KEY')
        self.url = current_app.config.get('SOCIAL_MEDIA_API_URL')

    def get_social_presence(self, domain):

        # The "Major" platforms we want to track
        target_platforms = ["facebook", "instagram", "linkedin", "twitter", "youtube", "tiktok", "pinterest", "github", "snapchat"]
    
        headers = {"x-api-key": self.api_key}
    
        try:
            response = requests.get(f"{self.url}/social-links-search/search-social-links", headers=headers, params={"query": domain})

            if response.status_code == 200:
                result = response.json()
                # The API returns a dictionary of social links (e.g., {"facebook": "url", ...})
                found_links = result.get('data', {})

                # Filter found links to only count our target platforms
                matches = [p for p in target_platforms if found_links.get(p)]

                # Calculate percentage
                presence_pct = (len(matches) / len(target_platforms)) * 100

                return {
                    "domain": domain,
                    "presence_percentage": f"{presence_pct}%",
                    "platforms_found": matches,
                    "score": presence_pct
                }
            else:
                return {"error": f"Status {response.status_code}"}

        except requests.exceptions.RequestException as e:
            return {"error": "request_failed", "details": str(e)}
