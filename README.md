# FluentScrapper
Basically web crawler!


### How To use ###

```java
  FluentScrapper.<String>of("https://site.com")
                .startPage("/some/path/")
                .linksContainsAny("work", "user") //optional restriction for url 
                .linksLimit(1000)
                .depthLevel(3)
                .omitNullData() 
                .fetcher(Fetchers.email()) // can use own fetchers
                .dataStore(DataStores.csvFile("~/user/myData.csv", ImmutableList::of))  // Save to 
                .crawl();
```


### How I use ###
Use for getting emails or phones. :D

### Install ###
Well if you need that give me an info. I will publish it to the mvn repo. 
