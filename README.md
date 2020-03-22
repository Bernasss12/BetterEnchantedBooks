# Better Enchanting Books

This is a mod that adds tries to add a couple of quality of life features into the Vanilla+ experience in Minecraft.
It is 100% client sided, so the server doesn't need to have the mod installed for it to work.
I'll try to keep everything as customizable as possible.

## Features

![2020-03-21_22 51 02](https://user-images.githubusercontent.com/6233500/77241366-0265cf00-6be9-11ea-9ef4-d4b667513b75.png)

 - Sort the enchantments in a tooltip, you can manually make your desired order or just alphabetically sort it;
 - Assign custom colors to each enchantment and those will display in as the color of the strip on Enchanted Books.

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
