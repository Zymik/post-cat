# Post cat

## Application for posting

### Idea

The main idea of project to create an application for making post on different platform with telegram bot as user interface. 
It can help when have many social networks, and you want to post something to all of them. 

In terms of project composition of places where message should post called 'Group'

### Current status
Now implemented three services
1. Core service for managing posts and groups
2. Telegram bot for creating new groups, adding channels to groups, post to public channels
3. Public api for getting post of groups

### Plans
1. Create services for posting to other platforms like [Discord](https://discord.com/) or [VK](https://vk.com/)
2. Add support of posts scheduling
3. Maybe add small frontend writen in Rust with [Yew](https://yew.rs/)

### Commands DSL
While developing telegram bot was implemented Commands DSL. Suppose of Commands DSL is parsing argument of bot commands easily. Now I use it for parsing telegram messages and
it can be used for parsing arguments of other platforms messages.

Format of commands:
```scala
 / commandName / commandDescription / argsParse [~| contextResolver] ~> commands | ...
```

context resolver put in brackets, because it can be resolved implicitly

Example from sources:
```scala
  private val commands: BotCommands[F] = {
      "/start" / "Register in post service" / arg[Unit] ~> register |
      "/create_group" / "Create group with name" / arg[String] ~> groupController.createGroup |
      "/add_channels" / "Add channels to group" / arg[String] ~ arg[NonEmptyList[TelegramChannelId]] ~> addChannels |
      "/post" / "Post message to group" / arg[String] ~| (userIdContext product replyContext) ~> postToGroup |
      "/public_to_rest_api" / "Make available to get posts from rest api" / arg[String] ~> publicToRestApi |
      "/close_to_rest_api" / "Make unavailable to get posts from rest api" / arg[String] ~> closeToRestApi
  }
```

### Technologies
- [Scala 2.13](https://www.scala-lang.org/) - backend language
- [Cats](https://typelevel.org/cats/) - functional programming abstractions
- [Cats Effect 3](https://typelevel.org/cats-effect/) - IO Monad for capturing and controlling 'effects'
- [Cats parse](https://github.com/typelevel/cats-parse) - parser combinators for parsing bot commands
- [Http4s](https://http4s.org/#) - http client/server for cats effect
- [Tapir](https://tapir.softwaremill.com/en/latest/) - writing endpoints as data types
- [Telegramium](https://github.com/apimorphism/telegramium) - telegram api cover based on http4s, cats effect and circe
- [Doobie](https://github.com/tpolecat/doobie) - database access library for cats effect based on jdbc
- [Flyway](https://flywaydb.org/) - database migrations
- [ScalaTest](https://www.scalatest.org/)/[ScalaMock](https://scalamock.org/) - testing and mocking
- [Circe](https://circe.github.io/circe/) - JSON encoding/decoding
- [Ciris](https://cir.is/) - configuration library
