class Config(object):
    DEBUG = False
    TESTING = False
    
    IMAGE_UPLOADS = "app/static/img"


class ProductionConfig(Config):
    pass

class DevelopmentConfig(Config):
    DEBUG = True
    SESSION_COOKIE_SECURE = False


class TestingConfig(Config):
    TESTING = True
    SESSION_COOKIE_SECURE = False

