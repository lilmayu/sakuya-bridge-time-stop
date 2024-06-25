# Sakuya Bridge: Time Stop

A fancy bridge to cross between the realm of local networks and the Internet.

> [!IMPORTANT]
> This is a rewrite of original Sakuya Bridge. The communication model between the server and the client is in TCP.
>
> All the work is now currently in the v2 branch.

## What is this?

Sakuya Bridge is a collection of applications that work together to provide a user-friendly
and secure way to play Touhou 9 Phantasmagoria of Flower View over the Internet without
the need of port forwarding.

## Downloads
Coming soon (TM)

## Disclaimers

> Touhou Project (東方Project) is a series of Japanese bullet hell shooter video games developed
> by the sole Team Shanghai Alice member ZUN.
>
> Touhou 9 Phantasmagoria of Flower View is a game developed by Team Shanghai Alice.
>
> **This project is not affiliated with Team Shanghai Alice nor ZUN in any way.**

> This project does not actually provide any patches to the game.
> It only provides a way to connect to other players over the Internet.
>
> **In the back-end, it still uses adonis/adonis2.**

> This project is still in development and is not ready for use yet.

## What's inside?
- Sakuya Bridge Server (`./modules/server`)
- Sakuya Bridge Client (`./modules/client`)

### Sakuya Bridge Server
Sakuya Bridge Server is a server application that runs on the Internet and acts as a bridge.

To this server, Touhou 9 **non-hosting players are connected directly** (e.g. no external software is required)
and hosting players are connected with Sakuya Bridge Client.

### Sakuya Bridge Client
Sakuya Bridge Client is a client application that allows you to easily host games and connect to other players with GUI.

Features include:
- Easy-to-use GUI
- Hosting games
- Connecting to other players
- Browsing public games
- Creating random matches
- and more... maybe.

#### Technically speaking...
Sakuya Bridge Client is a wrapper around adonis/adonis2 that provides a user-friendly way to use it
with additional support for browsing public games created using Sakuya Bridge Client.

**Player, who does not host the game, does not have to use this application** to connect to the game - they can **use
an IP and port to connect manually**, which will be supplied by the hosting player.

#### If you are a hosting player...
Sakuya Bridge Client is used to receive data from the Sakuya Bridge Server
and forward it to the game and vice versa.

## Documentation and wikis
Coming soon (TM)

## Staging artifacts
Staging artifacts are available on every push to `main` branch.

[You can find them here](https://github.com/lilmayu/sakuya-bridge-time-stop/actions)

Both, Java and C#, build results are there in their respective workflows and zip files.

> ### Disclaimer
> These artifacts are not meant to be used by end-users. They are meant for testing purposes only.
>
> Please refer to the [Downloads](#downloads) section for the official releases.
