# 실제 서버 설정

from config.util import get_server_info_value
from config.settings.base import *


SETTING_PRD_DIC = get_server_info_value("production")
SECRET_KEY = SETTING_PRD_DIC["SECRET_KEY"]

DEBUG = False

ALLOWED_HOSTS = ['mywebsite.com']

DATABASES = {
    'default': SETTING_PRD_DIC['DATABASES']["default"]
}