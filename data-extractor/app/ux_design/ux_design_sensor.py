from flask import current_app
import requests


class UXDesignSensor:

    def __init__(self):
        self.api_key = current_app.config.get('PAGE_SPEED_API_KEY')
        self.api_url = current_app.config.get('PAGE_SPEED_API_URL')

    def fetch_data(self, url):
        # Implement the logic to fetch UX design data from the API
        params = {'url': url,'key': self.api_key,'strategy': 'desktop','category': ['performance', 'accessibility', 'seo', 'best-practices']}

        try:
            response = requests.get(self.api_url, params=params)
            response.raise_for_status() # Check for HTTP errors
            data = response.json()
            
            # Extracting scores (multiplied by 100 for standard format)
            categories = data['lighthouseResult']['categories']
        
            results = {
            'Performance': categories['performance']['score'] * 100,
            'Accessibility': categories['accessibility']['score'] * 100,
            'SEO': categories['seo']['score'] * 100,
            'Best Practices': categories['best-practices']['score'] * 100
            }
        
            return results

        except Exception as e:
            return f"Error: {e}"

    def analyze_data(self, data):
        # Implement the logic to analyze the fetched data and extract insights
        pass