# Fewcam

Fewcam is a website evaluation prototype composed of two services:

1. **A Python website data extractor** that collects security, SSL, reputation,
   transparency, and UX signals from a domain.
2. **A Java fuzzy-inference API** that combines numeric inputs and fuzzy rules
   into a final evaluation score.

The Java application is backed by the `efis` and `ecorefewcam` Eclipse
projects. The repository currently contains Eclipse/WildFly project metadata
rather than a Maven or Gradle build.

## Repository layout

```text
.
├── data-extractor/              # Flask service and website sensors
│   ├── app/
│   │   ├── reputation/          # Domain age, authority, and social presence
│   │   ├── security/            # Security headers and SSL/TLS health
│   │   ├── transparency/        # Contact, policy, and ToS;DR analysis
│   │   └── ux_design/           # PageSpeed/Lighthouse scores
│   ├── config.py                # Environment-backed Flask configuration
│   ├── headers.json             # Weighted security-header rules
│   └── main.py                  # Flask development entry point
├── src/main/java/               # Java JAX-RS fuzzy-inference API
├── src/main/webapp/             # Servlet/CDI/MicroProfile configuration
├── .classpath                   # Eclipse classpath and project dependencies
└── .project                     # Eclipse project metadata
```

Generated Python virtual-environment files, Python bytecode, and Java build
output are not part of the application source.

## Architecture

The data extractor exposes one Flask blueprint. Each route delegates to a
sensor:

| Area         | Sensor                                              | External dependency               |
| ------------ | --------------------------------------------------- | --------------------------------- |
| Security     | Security headers                                    | Target website                    |
| Security     | SSL certificate health                              | SSL Labs API                      |
| Reputation   | Domain age                                          | WHOIS                             |
| Reputation   | Domain authority                                    | Configured domain-authority API   |
| Reputation   | Social-media presence                               | Configured social-links API       |
| Transparency | Contact information                                 | WHOIS                             |
| Transparency | Privacy, refund, and terms policies                 | Target website, Playwright, spaCy |
| Transparency | ToS;DR policy rating                                | ToS;DR API                        |
| UX           | Performance, accessibility, SEO, and best practices | Google PageSpeed Insights API     |

The Java service receives linguistic variables and fuzzy rules, initializes an
EFIS model, validates the model graph with Eclipse OCL, runs inference, and
defuzzifies the output with a centroid algorithm.

## Python data-extractor

### Requirements

The source uses Python packages including:

- Flask
- `requests`
- `httpx`
- `python-whois`
- `python-dateutil`
- BeautifulSoup (`bs4`)
- Playwright
- spaCy
- the spaCy `en_core_web_sm` model

There is currently no committed `requirements.txt` or `pyproject.toml`.
Install the dependencies in a virtual environment using the package manager
and versions appropriate for your environment. Playwright also requires its
browser runtime, and spaCy requires the English model.

Example setup:

```bash
cd data-extractor
python -m venv .venv
source .venv/bin/activate
pip install flask requests httpx python-whois python-dateutil beautifulsoup4 \
  playwright spacy
python -m playwright install chromium
python -m spacy download en_core_web_sm
```

### Configuration

Configuration is read from environment variables. The default headers file is
`headers.json`; set `HEADERS_FILE` when running from a different working
directory or with a different rules file.

| Variable                   | Used by                    | Purpose                              |
| -------------------------- | -------------------------- | ------------------------------------ |
| `HEADERS_FILE`             | Security headers           | Path to weighted header rules        |
| `X_API_KEY`                | Social-media presence      | API key for the social-links service |
| `SOCIAL_MEDIA_API_URL`     | Social-media presence      | Base URL of the social-links API     |
| `PAGE_SPEED_API_KEY`       | UX design                  | Google PageSpeed Insights API key    |
| `PAGE_SPEED_API_URL`       | UX design                  | PageSpeed API endpoint               |
| `DOMAIN_AUTHORITY_API_URL` | Domain authority           | Domain-authority API endpoint        |
| `TOSDR_DEV_URL`            | ToS;DR in development      | ToS;DR API base URL                  |
| `TOSDR_PROD_URL`           | ToS;DR outside development | ToS;DR API base URL                  |

Do not commit API keys. Export them in the shell or provide them through a
local, untracked environment file.

### Running the service

From `data-extractor/`:

```bash
source .venv/bin/activate
python main.py
```

The application factory supports `development` and `default` configuration
profiles. The default Flask development server is normally available at
`http://127.0.0.1:5000`.

### HTTP endpoints

All endpoints are `GET` endpoints and return JSON. Website URLs may be supplied
with or without `http://` or `https://` where noted.

| Endpoint                        | Query parameter | Result                                                               |
| ------------------------------- | --------------- | -------------------------------------------------------------------- |
| `/sense-security-headers`       | `url`           | Weighted security-header score                                       |
| `/sense-ssl-certificate-health` | `url`           | SSL/TLS endpoint scores                                              |
| `/sense-policies`               | `url`           | Extracted policy-page text by category                               |
| `/sense-social-media-presence`  | `url`           | Matching social platforms and percentage                             |
| `/sense-domain-age`             | `domain`        | WHOIS creation date and domain age                                   |
| `/sense-contact-information`    | `domain`        | WHOIS contact fields and completeness score                          |
| `/sense-ux-design`              | `url`           | PageSpeed performance, accessibility, SEO, and best-practices scores |
| `/sense-tosdr-policies`         | `service_name`  | ToS;DR service policy rating                                         |
| `/sense-domain-authority`       | `domain`        | Domain-authority API response                                        |

Examples:

```bash
curl "http://127.0.0.1:5000/sense-security-headers?url=example.com"
curl "http://127.0.0.1:5000/sense-domain-age?domain=example.com"
curl "http://127.0.0.1:5000/sense-ux-design?url=https://example.com"
```

The policy endpoint launches a headless Chromium browser and may take
significantly longer than the other sensors. External services can fail or
rate-limit requests; callers should handle the documented HTTP error responses.

## Java fuzzy-inference API

### Requirements

The Eclipse project metadata targets:

- Java 8
- WildFly 21
- Servlet/JAX-RS, CDI, Eclipse EMF, and Eclipse OCL
- The sibling Eclipse projects `efis` and `ecorefewcam`

Import the repository and its sibling projects into Eclipse, configure a
WildFly 21 runtime, and ensure the `efis` and `ecorefewcam` projects are
available on the build path. The application is packaged as a web application
and is deployed to the configured application server.

### API

The JAX-RS application is mounted below `/api`. The evaluation resource is:

```text
POST /api/evaluate
Content-Type: application/json
Accept: application/json
```

Request body:

```json
{
  "linguisticVariableData": [
    {
      "name": "security",
      "min": 0,
      "max": 100,
      "value": 82
    },
    {
      "name": "transparency",
      "min": 0,
      "max": 100,
      "value": 65
    }
  ],
  "outputVariable": {
    "name": "credibility",
    "min": 0,
    "max": 100,
    "value": 0
  },
  "ruleData": [
    {
      "e1": "high_security",
      "e2": "high_transparency_and_medium_security",
      "e1Size": 1,
      "e2Size": 2,
      "output": "high_credibility"
    }
  ]
}
```

The implementation defines `low`, `medium`, and `high` triangular membership
functions over a 0–100 range, along with `very`, `slightly`, and `just`
linguistic hedges. Rules are combined with the minimum (`and`) aggregator and
the result is defuzzified using a centroid algorithm.

Successful responses have the following shape:

```json
{
  "variableName": "credibility",
  "score": 78.4
}
```

The exact rule keys must match the predicate names generated from the input
variables and terms. Invalid EFIS/OCL graphs cause the evaluation request to
fail rather than returning a score.

## Development notes

- The repository does not currently define automated tests.
- The Java build depends on Eclipse workspace projects and server runtime
  configuration not included in this repository.
- `headers.json` assigns a total possible security-header score of 100.
- `pages_speed.json` is a captured PageSpeed response and is useful as sample
  data, not as a runtime configuration file.
- The policy analyzer uses spaCy for structural metrics and Playwright for
  JavaScript-rendered pages.

## License

This project is licensed under the GNU GENERAL PUBLIC LICENSE
