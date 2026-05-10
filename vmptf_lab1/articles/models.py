from django.db import models

# Create your models here.
class Article(models.Model):
    title = models.CharField(max_length=200)
    text = models.TextField()
    author = models.CharField(max_length=100)

    category = models.ForeignKey('category.Category', on_delete=models.CASCADE)

    def __str__(self):
        return self.title