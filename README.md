[ ![Discord Support](https://www.mediafire.com/convkey/510b/iw2k26exg0qlf076g.jpg) ](https://discord.gg/p5DAvc6)
[ ![Bugs, Issues, Feature Requests](https://www.mediafire.com/convkey/2320/x80qtabf3auhhjr6g.jpg) ](../../issues)
[ ![Donate](https://www.mediafire.com/convkey/910d/z8160kkzvezi4km6g.jpg) ](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=THXHQ5287TBA8)

# ResponseBot

Spigot 1.11 plugin to answer questions your players might ask in chat. Uses API.ai


# Description
Note: This requires Java 1.8+

This will use API.ai to respond to possible player questions in game.

# Usage

Create an account with https://API.ai and create Intents(Questions players may ask with text responses)

Note:
When creating text responses to your Intents you can use color codes ( &1, &2, etc ) and you can use [DEFAULT] to insert the defaultColor config option(This is useful when you want to reset your text color but you aren't sure if you may change it in your config in the future) you can also use [PLAYER] to insert the players name in a response

# Permissions

[View permissions here](../../blob/master/src/plugin.yml)


# Installation

- Create an account at https://API.ai
- Create a new Agent
- Create a simple Intent
- - A user expression could be Hello
- - A text response could be Hey there!


- Place the jar file in your plugins folder
- Restart your server
- Edit the config file and update your accessToken(You can get that by click on the settings icon for your new Agent on API.ai)
- Restart your server

# Configuration
**replyToPlayer**: false

- If **false**, this will send the response to the entire server as a broadcast, if **true** it will send the response directly to the player


**requiredPrefix**: ""

- If this is set to something such as **"?"**, the bot will only listen to chat that begins with ? (So a player could type ?what is our website), if left blank the bot will listen to everything


More config options here:

[View default configuration file here](../../blob/master/src/config.yml)
