# Better Enchanted Books [<img alt="Fabric API Required" src="https://i.imgur.com/Ol1Tcf8.png" height="20" />](https://www.curseforge.com/minecraft/mc-mods/fabric-api)


This is a mod that adds a couple of quality of life features into the Vanilla+ experience in Minecraft.
It is 100% client sided, so the server doesn't need to have the mod installed for it to work.
I'll try to keep everything as customizable as possible.

## Features

![SampleImage](https://user-images.githubusercontent.com/6233500/77241366-0265cf00-6be9-11ea-9ef4-d4b667513b75.png)

 - Sort the enchantments in a tooltip, you can manually make your desired order or just alphabetically sort it;
 - Assign custom colors to each enchantment and those will display in as the color of the strip on Enchanted Books.

### Notes:

 - [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu/) is not necessary but recommended _and_ the only way to change any settings ingame.
 - The custom sorting order is still not implemented but should be soon™.

## For dev:

1. Edit build.gradle and mod.json to suit your needs.
    * The "mixins" object can be removed from mod.json if you do not need to use mixins.
    * Please replace all occurences of "modid" with your own mod ID - sometimes, a different string may also suffice.
2. Run the following command:

```
./gradlew idea
```

## License

Please read it at [licence](LICENCE).
