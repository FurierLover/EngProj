class Config(object):
    DEBUG = True
    TESTING = False
    IMAGE_UPLOADS = "uploaded_images"


class ProductionConfig(Config):
    pass


class DevelopmentConfig(Config):
    DEBUG = True
    SESSION_COOKIE_SECURE = False


class TestingConfig(Config):
    TESTING = True
    SESSION_COOKIE_SECURE = False
