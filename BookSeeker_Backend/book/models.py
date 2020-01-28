from django.db import models


# ComicInfo() : Comic Information Table을 정의하는 클래스
class ComicInfo(models.Model):
    id = models.AutoField(primary_key=True) # Division Primary Key(auto increasement)
    bsin = models.CharField(unique=True, max_length=100) # Book Specific Identification Number
    title = models.CharField(max_length=100) # Comic Book Title
    author = models.CharField(max_length=100) # Comic Book Author
    publisher = models.CharField(max_length=100) # Comic Book Publisher
    introduction = models.TextField(null=True)  # Comic Book Introduction
    image = models.CharField(unique=True, max_length=100) # Comic Book Image
    link = models.CharField(unique=True, max_length=100) # Comic Book Link
    keyword = models.TextField(null=True) # Comic Book Keyword
    adult = models.CharField(max_length=10) # Comic Book Constrict
    seperator = models.CharField(max_length=100) # Comic Book Seperator
    date = models.DateField(null=True)  # Comic Book Publication Date

    # __str__(self) : Table의 각 Tuple을 대표하는 값을 지정
    def __str__(self):
        return self.id

# RomanceInfo() : Romance Information Table을 정의하는 클래스
class RomanceInfo(models.Model):
    id = models.AutoField(primary_key=True) # Division Primary Key(auto increasement)
    bsin = models.CharField(unique=True, max_length=100) # Book Specific Identification Number
    title = models.CharField(max_length=100) # Romance Book Title
    author = models.CharField(max_length=100) # Romance Book Author
    publisher = models.CharField(max_length=100) # Romance Book Publisher
    introduction = models.TextField(null=True)  # Romance Book Introduction
    image = models.CharField(max_length=100) # Romance Book Image
    link = models.CharField(max_length=100) # Romance Book Link
    keyword = models.TextField(null=True) # Romance Book Keyword
    adult = models.CharField(max_length=10) # Romance Book Constrict
    seperator = models.CharField(max_length=100) # Comic Book Seperator
    date = models.DateField(null=True)  # Romance Book Publication Date

    # __str__(self) : Table의 각 Tuple을 대표하는 값을 지정
    def __str__(self):
        return self.id

# FantasyInfo() : Fantasy Information Table을 정의하는 클래스
class FantasyInfo(models.Model):
    id = models.AutoField(primary_key=True) # Division Primary Key(auto increasement)
    bsin = models.CharField(unique=True, max_length=100) # Book Specific Identification Number
    title = models.CharField(max_length=100) # Fantasy Book Title
    author = models.CharField(max_length=100) # Fantasy Book Author
    publisher = models.CharField(max_length=100) # Fantasy Book Publisher
    introduction = models.TextField(null=True)  # Fantasy Book Introduction
    image = models.CharField(max_length=100) # Fantasy Book Image
    link = models.CharField(max_length=100) # Fantasy Book Link
    keyword = models.TextField(null=True) # Fantasy Book Keyword
    adult = models.CharField(max_length=10) # Fantasy Book Constrict
    seperator = models.CharField(max_length=100) # Comic Book Seperator
    date = models.DateField(null=True)  # Romance Book Publication Date

    # __str__(self) : Table의 각 Tuple을 대표하는 값을 지정
    def __str__(self):
        return self.id

class ComicRatingInfo(models.Model):
    id = models.AutoField(primary_key=True) # Division Primary Key(auto increasement)
    email = models.EmailField(max_length=32) # User Email
    bsin = models.CharField(max_length=100) # Book Sepcific Identification Number
    rating = models.FloatField(null=True, default=0.0) # Book Rating

    # __str__(self) : Table의 각 Tuple을 대표하는 값을 지정
    def __str__(self):
        return self.id

class RomanceRatingInfo(models.Model):
    id = models.AutoField(primary_key=True) # Division Primary Key(auto increasement)
    email = models.EmailField(max_length=32) # User Email
    bsin = models.CharField(max_length=100) # Book Sepcific Identification Number
    rating = models.FloatField(null=True, default=0.0) # Book Rating

    # __str__(self) : Table의 각 Tuple을 대표하는 값을 지정
    def __str__(self):
        return self.id

class FantasyRatingInfo(models.Model):
    id = models.AutoField(primary_key=True) # Division Primary Key(auto increasement)
    email = models.EmailField(max_length=32) # User Email
    bsin = models.CharField(max_length=100) # Book Sepcific Identification Number
    rating = models.FloatField(null=True, default=0.0) # Book Rating

    # __str__(self) : Table의 각 Tuple을 대표하는 값을 지정
    def __str__(self):
        return self.id