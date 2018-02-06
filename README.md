# GitHubRepoBrowser
This project demonstrates the usage of RxJava 2, Retrofit 2 and ObjectBox. Before starting with hands on I would suggest to go through the basics of RxJava and why we should use it. You can learn these from the links provided. With the help of RxJava, code behaves more like functional way and easy to understand. As the callbacks encapsulated inside Observable declarations the program flow remains intact and solves the callback hell issue we face in Android projects. Too much usage of callbacks results in GOTO like statements which breaks the flow of code execution. Rather we should focus more on optimizing the flow and making the code more readable.

This example also includes usage of ObjectBox and relation between two Entities. Here the two main entities are GitHubRepo (list of repos) and Owner (of a repo). One Owner can have multiple repositories hence `1:N` (ToMany) relation and similarly one Repository can belong to one Owner hence `1:1` relation (ToOne). And by using `@Backlink` it is super easy to get the list of repos from Owner box. See `OwnerDAO.java` for more clarity. 

Why ObjectBox ? Well the answer is really simple. Because it's much faster and easier to use. As this is based on NoSQL the CRUID operations are much faster. Ideally we should always separate the DB layer from UI layer and UI layer should only deal with objects and that is why ORM is always recommended but after introduction of ObjectBox this becomes way too easy. Less code is better code after all.

RxJava Fundamentals : https://blog.mindorks.com/rxjava-anatomy-what-is-rxjava-how-rxjava-is-designed-and-how-rxjava-works-d357b3aca586
<br />RxJava Basics : https://mindorks.com/course/learn-rxjava
<br />RxJava Hands On : http://www.vogella.com/tutorials/RxJava/article.html 
<br />ObjectBox Documentation : http://objectbox.io/documentation/
<br />ObjectBox vs Realm vs Room : https://notes.devlabs.bg/realm-objectbox-or-room-which-one-is-for-you-3a552234fd6e
<br />Retrofit : http://square.github.io/retrofit/

Next steps (coming soon) : Making the API request lifecycle aware using Repository pattern. More details here : https://www.bignerdranch.com/blog/the-rxjava-repository-pattern/ 

<br />Happy Coding :)
<br />Fork -> Improve -> Share -> Repeat


