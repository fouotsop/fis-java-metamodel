import requests

class DomainRatingSensor:
    def __init__(self, api_url: str):
        self.api_url = api_url
        self.headers = {"Accept": "application/json"}

    def get_domain_rating(self, domain):

        params = {
            "target": domain,
            "output": "json"
        }

        try:
            response = requests.get(self.api_url, headers=self.headers, params=params)
            response.raise_for_status()
            data = response.json()
            return data
        except requests.exceptions.RequestException as e:
            return {"error": str(e)}