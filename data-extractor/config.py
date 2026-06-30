import os

class Config:
    HEADERS_FILE = os.environ.get('HEADERS_FILE', 'headers.json')

    X_API_KEY = os.environ.get('X_API_KEY')
    
    PAGE_SPEED_API_KEY = os.environ.get('PAGE_SPEED_API_KEY')

    PAGE_SPEED_API_URL = os.environ.get('PAGE_SPEED_API_URL')

    SOCIAL_MEDIA_API_URL = os.environ.get('SOCIAL_MEDIA_API_URL')

    TOSDR_URL = os.environ.get('TOSDR_PROD_URL')

    DEBUG = False

    DOMAIN_AUTHORITY_API_URL = os.environ.get('DOMAIN_AUTHORITY_API_URL')

class DevelopmentConfig(Config):

    DEBUG = True

    TOSDR_URL = os.environ.get('TOSDR_DEV_URL')


config_options = {
    'development': DevelopmentConfig,
    'default': Config
}