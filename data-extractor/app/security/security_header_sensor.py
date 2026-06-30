import json
import warnings

import requests
from flask import current_app
from requests.exceptions import RequestException


class SecurityHeaderSensor:

    _headers: dict

    def __init__(self):
        header_file = current_app.config.get('HEADERS_FILE', "headers.json")

        with open(header_file, 'r') as file:
            self._headers = json.load(file)


    def _extract_headers(self, url: str) -> list:
        try:
            response = requests.get(url, timeout=30, verify=True)
        except RequestException as exc:
            raise RuntimeError(f"Failed to fetch security headers from {url}: {exc}") from exc

        required_headers =  self._headers.keys()
        return [header for header in required_headers if header in response.headers]

    def _measure_header_presence(self, headers: list) -> float:

        score = 0
        for header, weight in self._headers.items():
            # Check if the header exists in the results
            if header in headers:
                score += weight
        return score

    def sense(self, url: str) -> float:
        headers = self._extract_headers(url)
        return self._measure_header_presence(headers)
