from django.contrib import admin

from book.models import ComicInfo
from book.models import RomanceInfo
from book.models import FantasyInfo


admin.site.register(ComicInfo)
admin.site.register(RomanceInfo)
admin.site.register(FantasyInfo)