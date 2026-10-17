import whois
from datetime import datetime, timezone
import dateutil.parser as parser



class DomainAgeSensor:
    def __init__(self):
        pass


    def sense(self, domain_name):
        try:
            # Perform WHOIS lookup
            w = whois.whois(domain_name)

            # WHOIS data can return a single date or a list of dates
            creation_date = w.creation_date
            if isinstance(creation_date, list):
                creation_date = creation_date[0]

            if not creation_date:
                return "Creation date not found."

            # Parse the date into a datetime object
            if isinstance(creation_date, str):
                created = parser.parse(creation_date)
            else:
                created = creation_date

            # Calculate age
            today = datetime.now()
            age = today.replace(tzinfo=timezone.utc) - created
            age_years = round(age.days / 365.25, 2)

            # Return structured data
            return {
                "domain": domain_name,
                "created": created.strftime('%Y-%m-%d'),
                "age_days": age.days,
                "age_years": round(age.days / 365.25, 2)
            }

        except Exception as e:
            return f"Error: {str(e)}"