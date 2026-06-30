

import asyncio

import httpx

class SSLCertificateHealthSensor:

    def __init__(self, url: str):
        self._url = url

    def calculate_endpoint_scores(self, api_response):
        """
        Calculates numerical scores for each endpoint in an SSL Labs API JSON response.
        Based on Qualys SSL Server Rating Guide.
        """
        results = []

        # ID Map for Protocols: SSL2=768, SSL3=769, TLS1.0=770, TLS1.1=771, TLS1.2=772, TLS1.3=773
        proto_score_map = {768: 0, 769: 80, 770: 90, 771: 95, 772: 100, 773: 100}

        for endpoint in api_response.get("endpoints", []):
            details = endpoint.get("details", {})
            if not details:
                continue

            # 1. Protocol Score: (Best + Worst) / 2
            proto_list = [proto_score_map.get(p.get("id"), 0) for p in details.get("protocols", [])]
            p_score = (max(proto_list) + min(proto_list)) / 2 if proto_list else 0

            # 2. Key Exchange Score: Tiered by bits
            kx_bits = details.get("keyInBits", 0)
            if kx_bits >= 4096: kx_score = 100
            elif kx_bits >= 2048: kx_score = 90
            elif kx_bits >= 1024: kx_score = 80
            elif kx_bits >= 512: kx_score = 40
            else: kx_score = 20

            # 3. Cipher Strength Score: (Best + Worst) / 2 of bit-strength tiers
            cipher_tiers = []
            for suite_list in details.get("suites", []):
                for suite in suite_list.get("list", []):
                    bits = suite.get("cipherStrength", 0)
                    if bits >= 256: cipher_tiers.append(100)
                    elif bits >= 128: cipher_tiers.append(80)
                    elif bits > 0: cipher_tiers.append(20)
                    else: cipher_tiers.append(0)

            c_score = (max(cipher_tiers) + min(cipher_tiers)) / 2 if cipher_tiers else 0

            # 4. Final Weighted Score: 30% Protocol, 30% Key Exchange, 40% Cipher
            total_score = (p_score * 0.3) + (kx_score * 0.3) + (c_score * 0.4)

            # Zero Penalty: If any category is 0, the total score is 0.
            if 0 in [p_score, kx_score, c_score]:
                total_score = 0

            results.append({
                "ipAddress": endpoint.get("ipAddress"),
                "category_scores": {
                    "protocol": p_score,
                    "key_exchange": kx_score,
                    "cipher": c_score
                },
                "total_score": round(total_score, 2),
                "original_grade": endpoint.get("grade")
            })

        return results

    # Example usage with your API 'data' variable:
    # final_scores = calculate_endpoint_scores(data)
    # for s in final_scores:
    #     print(f"IP: {s['ipAddress']} | Score: {s['total_score']} | Grade: {s['original_grade']}")


        
    async def _get_numeric_ssl_scores(self, hostname) -> dict:
        params = {
            "host": hostname,
            "startNew": "off",
            "fromCache": "on",
            "publish": "off",
            "ignoreMismatch": "off",
            "all": "done",
        }

        async with httpx.AsyncClient(timeout=30.0) as client:
            response = await client.get("https://api.ssllabs.com/api/v3/analyze", params=params)

        response.raise_for_status()
        host = response.json()

        if host.get("status") != "READY":
            raise Exception(f"SSL Labs analysis for {hostname} is not ready. Status: {host.get('status')}")   

        return self.calculate_endpoint_scores(host)


    def get_mean_ssl_score(self) -> dict:
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        ssl_scores = loop.run_until_complete(self._get_numeric_ssl_scores(self._url))
        loop.close()

        if not ssl_scores:
            return {"score": 0}

        total_score = sum(score['total_score'] for score in ssl_scores)
        return {"score": total_score // len(ssl_scores)}

    def sense(self) -> dict:
        try:
            return self.get_mean_ssl_score()
        except Exception as exc:
            raise RuntimeError(f"Failed to analyze SSL certificate health for {self._url}: {exc}") from exc

