# 개발 설정

from config.util import get_server_info_value
from config.settings.base import *


SETTING_PRD_DIC = get_server_info_value("development")
SECRET_KEY = SETTING_PRD_DIC["SECRET_KEY"]

DEBUG = True

ALLOWED_HOSTS = [
    "127.0.0.1",
    "localhost",
]

DATABASES = {
    'default': SETTING_PRD_DIC['DATABASES']["default"]
}