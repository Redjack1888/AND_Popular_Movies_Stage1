# Popular Movies (Stage 1)

This is Popular Movies Stage 1 of Udacity's Android Developer Nanodegree.
The purpose of this project was to built an app that helps users discover popular and top rated movies on the web.
It fetches themoviedb.org API to display the movies data, that way the content provided is always up-to-date and relevant.

Popular Movies stage 2 COMING SOON.

## Stage 1 Features

This is only the first part of the final Popular Movies Project.
It contains the following features:

- Upon launch, it presents the user with an grid arrangement of movie posters and titles
- Configurable movies sort order via Settings (Popular and Top Rated)
- Selecting a movie displays more detailed infomation such as: original title, plot synopsis, user rating, release date


## Instructions

You need to create a free account on themoviedb.org and generate your personal API key. More info [HERE](https://www.themoviedb.org/documentation/api).

I have used infos by this article [Best Way to Store your Api Keys for your Android Studio Project](https://technobells.com/best-way-to-store-your-api-keys-for-your-android-studio-project-e4b5e8bb7d23) to store my Apikey, so in your gradle.properties file, put your generated API Key like this: `TheMovieDbApiKey="COPY YOUR APIKEY HERE"`

## Libraries

This project uses the following libraries:

[Glide](https://github.com/bumptech/glide)
