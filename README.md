# EzQuesting
EzQuesting is just a trail project and is not for production uses.

Plugin supports versions from 1.8.x all the way to 1.17.x

A Quest plugin designed to be easy to use and being user-friendly.

You can use my own [quests.yml](https://github.com/ItzAmirreza/EzQuesting/blob/master/src/main/resources/quests.yml) file to see how the quests configuration works.

Compiled version is downloadable from [here](https://cdn.discordapp.com/attachments/902581431019135029/912845176651804682/EzQuesting-1.0.jar).

**More technical description:**

Plugin uses 2 types of data saving method, for quests and for user data. 
User data is being saved in mongodb(so it is required), and for quests data, in the quests.yml configuration file.

Plugin supports 5 objectives (Killing, Breaking, Placing, Walking and execution of command).
Each quests can have infinite amount of phases, and each phase can have only one objective. Additionally, to make to more realistic, configurator is free to make conversation before & after the phases, and can execute commands at the end of each phase.

Plugin has a main loop, which starts at the beginning of plugin initialization. This loop gets executed every tick, but not doing the job, in fact, having special timer within the same loop, for each job. Conversation is always a priority and blocks the continuation of the work in the QuestProcess object. QuestProcess object has the duty of handling everything related to a quest, when player is doing the quest. Each player can have infinite amount of QuestProcesses, as it depends on the amount of quests the player is doing at the same time.(Yes you can do multiple quests at the same time).

In the other hand, PlayerQuestData, is a way of caching the data, in order to decrease the usage of external database(mongodb), whenever it is not needed.(Although mongodb doesn't have a big (bad) impact on performance, this can still save a lot of resources when the amount of players increase).

**External Utilities**:
Starting with the GUI, I always prefer a single, and I think the best GUI library ever created, as it is super fast, simple, optimized and secure in any way, and it is [Triumph GUI](https://github.com/TriumphTeam/triumph-gui) library which is created and maintained by dear [Matt](https://github.com/ipsk).

In addition, I have used only 2 classes of [XSeries utility](https://github.com/CryptoMorin/XSeries) (XMaterial & XSounds), to ensure that there won't be any incompatiblitiy for cross-versioning feature.
