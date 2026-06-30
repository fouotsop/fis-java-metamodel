from flask import Flask
from config import config_options
from .security.security_header_sensor import SecurityHeaderSensor

import spacy


def create_app(self, config_name='default'):
    app = Flask(__name__)
    app.config.from_object(config_options[config_name])

    nlp = spacy.load("en_core_web_sm", disable=["parser", "lemmatizer"])

    # Register routes
    from .routes import main_bp
    app.register_blueprint(main_bp)

    return app