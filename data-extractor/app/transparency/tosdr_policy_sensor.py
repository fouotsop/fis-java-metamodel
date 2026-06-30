import requests

from flask import current_app


class TOSDRPolicySensor:

    tosdr_url: str

    def __init__(self, tosdr_url):
        self.tosdr_url = current_app.config.get('TOSDR_URL')
        self.tosdr_url = tosdr_url if tosdr_url else self.tosdr_url

    def search_service_by_name(self, service_name: str) -> list:
        search_url = f"{self.tosdr_url}/search/v5?query={service_name}"
        try:
            response = requests.get(search_url)
            response.raise_for_status()
            search_data = response.json()
            services_found = search_data.get("services", [])

            return services_found

        except Exception as e:
            print(f"Error during search: {e}")
            raise Exception(f"Error during search: {e}")

    def match_service(self, service_name: str, services_list: list) -> dict:
        for service in services_list:
            if service_name.lower() in service.get("urls", []):
                return service
        return None

    def find_service_by_name(self, service_name: str) -> dict:
        services_found = self.search_service_by_name(service_name)
        if not services_found:
            raise Exception(f"No services found matching '{service_name}'")

        matched_service = self.match_service(service_name, services_found)
        if not matched_service:
            raise Exception(f"No exact match found for '{service_name}' in search results")

        return matched_service

    def find_service_by_id(self, service_id: int) -> dict:

        url = f"{self.tosdr_url}/service/v3/?id={service_id}"

        try:
            response = requests.get(url)
            response.raise_for_status()
            service_data = response.json()
            return service_data

        except Exception as e:
            print(f"Error fetching service by ID: {e}")
            raise Exception(f"Error fetching service by ID: {e}")

    def calculate_service_score(self, service_data: dict) -> float:
        # Placeholder for actual scoring logic based on TOSDR data
        # This could involve analyzing the service's ratings, categories, and other attributes

        points = service_data.get("points", [])
           
        # If there are absolutely no community cases tracked yet, we can't score it safely
        if not points:
            return {
                "service_name": service_data.get("name", "Unknown Service"),
                "status": "Unrated",
                "grade": "Grade N/A (No data points available)",
                "numerical_score": 100
            }

        counts = {"good": 0, "neutral": 0, "bad": 0, "blocker": 0}

        # Process and calculate weights based on the case status strings
        n = 0
        for point in points:
            point_case = point.get("case", {})

            if not point_case:
                continue  # Skip if there's no case data

            n += 1
            classification = point_case.get("classification").lower()

            if classification == "good":
                counts["good"] += 1
            elif classification == "neutral":
                counts["neutral"] += 1
            elif classification == "bad":
                counts["bad"] += 1
            elif classification == "blocker":
                counts["blocker"] += 1
    
        service_inputs100 = {"good": counts["good"] * 100 / n, "bad": counts["bad"] * 100 / n, "neutral": counts["neutral"] * 100 / n, "blocker": counts["blocker"] * 100 / n}

        weight_good = 0.645771
        weight_bad = 0.083551
        weight_neutral = 0.229969
        weight_blocker =0.040709

        policy_score = 0.0

        policy_score = sum([service_inputs100["good"] * weight_good, service_inputs100["bad"] * weight_bad, service_inputs100["neutral"] * weight_neutral, service_inputs100["blocker"] * weight_blocker])
        return policy_score


    def sense_service_policy(self, service_name: str) -> dict:
        try:
            service_data = self.find_service_by_name(service_name)
            complete_service_data = self.find_service_by_id(service_data.get("id"))
            policy_score = self.calculate_service_score(complete_service_data)

            return {
                "service_name": complete_service_data.get("name", "Unknown Service"),
                "status": "Rated",
                "grade": f"Grade based on TOSDR data (Score: {policy_score})",
                "numerical_score": policy_score
            }

        except Exception as e:
            print(f"Error sensing service policy: {e}")
            raise Exception(f"Error sensing service policy: {e}")


