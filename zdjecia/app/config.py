class Config(object):
    DEBUG = True
    TESTING = False
    IMAGE_UPLOADS = "static/img"
    SECRET_KEY = 'my_secret'
    PATH_TO_MESHROOM = '/home/grobocop/meshroom/Meshroom-2019.2.0/meshroom_photogrammetry'


class ProductionConfig(Config):
    pass


class DevelopmentConfig(Config):
    DEBUG = True
    SESSION_COOKIE_SECURE = False


class TestingConfig(Config):
    TESTING = True
    SESSION_COOKIE_SECURE = False
