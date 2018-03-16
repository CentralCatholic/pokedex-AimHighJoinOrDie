# The Pokédex Lab

> Everyone knows Professor Oak wrote the first Pokédex as a Java GUI, which is why it was so jank back in 1995.

Your assignment is to build a Pokédex using Java GUIs. 
For anyone who is unaware, a Pokédex is a fictional device
from the world of Pokémon which lets the user lookup 
information about various Pokémon. It's Wikipedia but for
Google searching Pokémon.

Your Pokédex will support all 802 Pokémon. It will use a
custom class I have implemented to find and return
information about each Pokémon. You are only responsible
for displaying that information in a Java GUI.

## The PokeEntry Class

I have implemented a `PokeEntry` class. This class contains
all of the data available to you about each entry in the
Pokedex.

A `PokeEntry` object can be created in one of two ways.
First, you can pass in the Pokemon's ID number. As you
may know, each Pokemon is assigned a unique identifier.
Starting with Balbasaur at #1, Pikachu at #25, Mewtwo 
at #150, and so on, passing an integer to the `PokeEntry`
constructor will return the Pokemon identied by that ID.
Your Pokedex must support a search bar allowing users to
lookup Pokemon by ID.

The second constructor for the `PokeEntry` class accepts
a String. This allows lookup by a Pokemon's name. Your
program must support a textbox allowing users to lookup
Pokemon by name. This textbox must be the same textbox as
above; you must have one textbox that accepts both IDs and
names.

I have populated a number of image fields. You can render 
images in your GUI using [this technique](https://docs.oracle.com/javase/tutorial/2d/images/drawimage.html).

# PokeEntry Fields

| Field Name   | Type                         | Accessor Method      | Description                            |
| ------------ | ---------------------------- | -------------------- | -------------------------------------- |
| name         | string                       | getName()            | Pokemon's name                         |
| height       | int                          | getHeight()          | Height, in inches                      |
| weight       | int                          | getWeight()          | Weight, in ounces??                    |
| id           | int                          | getID()              | Canonical Pokedex ID                   |
| types        | List<String>                 | getTypes()           | Types this Pokemon has                 |
| Flavor texts | List<String>                 | getFlavorTexts()     | Favor texts about this 'Mon'           |
| Evolves From | List<PokeEntry>              | getPriorEvolutions() | The 'mon this 'mon evolves from        |
| Evolves Into | List<PokeEntry>              | getEvolutions()      | The Pokemon this 'mon evolves into     |
| Image        | java.awt.Image.BufferedImage | getImage()           | The picture, as a PNG, of this Pokemon |
|              |                              |                      |                                        |

# REQUIREMENTS

There are minimal requirements for this program. However, you must:

1. Allow the user to search for a Pokemon by ID or by name, using only one text field.
2. Show an image of that Pokemon in the GUI.
3. Use a Layout other than the default `FlowLayout` _i.e._ consider using `GridLayout` or `BorderLayout`, and/or use advanced features of `FlowLayout`.
4. You must allow the user to see both the prior evolutions (if any) and the future evolutions (if any). Additionally, you must allow a user to navigate to those evolutions with the mouse.
5. You must use more than one color. The default color is not sufficient. See *Gaddis, 748* for more information.
6. You are expected to use either the anonymous type approach to implementing `ActionListener` or the Java 8 lambda approach. Only implementing `ActionListerners` as inner classes will cost you a few points.
7. You will be graded not only on the above functionality, but also on the look and feel of your program. Better looking programs will receive better grades. Programs that are unpleasantly looking, make insufficient use of the API, or that fail to be ergonomic will lose points. I don't expect everyone to get an A on this assignment. If you don't care significant care to make your Pokedex look clean and colorful, display as much information as possible, and be easy and intuitive to use, you can expect a B or lower on this assignment.
8. Your Pokédex is expected to make use of the different flavor texts; given that there are usually more than one, at very least you should randomly select a flavor text to show to the user each time they visit that Pokémon's entry. Alternatively, you can allow them to cycle through the different flavor texts. My guess is that you'll find there are more flavor texts for Pokémon released earlier, so don't be surprised if newer Pokémon only have a single flavor text.

Feel free to encode additional information into your Pokedex. For example:

- type matchups
- store a list of Pokemon the user has seen before, and allow them to quickly link to those Pokemon, perhaps in a different tab. Entering all of the Pokemon and thus completing the list is canonically known as "completing your Pokedéx". Fun fact: I completed my Sapphire Pokédex. Get at me.
- absolutely anything else you can think of: gym badges, trainer numbers; anything is fair game. Consider the in-game equiment for inspiration (*e.g.* [Pokégear from Johto](https://bulbapedia.bulbagarden.net/wiki/Pok%C3%A9gear), Pokénav from Hoenn]https://bulbapedia.bulbagarden.net/wiki/Pok%C3%A9Nav, [Pokétck from Sinnoh](https://bulbapedia.bulbagarden.net/wiki/Pok%C3%A9tch))

You won't be penalized for not encoding additional information, but you cannot receive extra credit for only implementing the basic features.

Providing additional functionality to your Pokédex will improve your score. I will not guarentee extra credit for completing the above. However, if I find that this assignment is popular enough that you put a lot of effort into it as a class, I can imagine awarding some extra points. (That is, if the class collectively doesn't put much effort in, there's less likely to be scores above 100% than if the class puts a lot of effort in; in which case, I would expect high grades all around).

# Notes

The API can be slow. Expect anywhere from a 1-5 second wait when you create a new PokeEntry object.

You must adjust your classpath to both compile and run this program. I have included two dependencies. I tried very hard to write `PokeEntry` without these dependencies, but I'm afraid that was impossible for me to do. To compile, you may have to run `javac -cp .:javax *.java`. I find I don't have to do that because my classpath include the currnet directory and thus my dependency. I do, however, need to adjust my classpath at runtime. I execute `Main` with `java -cp .:javax.json.jar Main`. If you use an IDE and need help setting up your classpath, I can help you. I would prefer you try to use the command line first before asking for help.

I have included a sample program in `Main.java` that demonstrates the `PokeEntry` class. You should write your program inside of the `Main.java` file.

If you are unfamilar with Pokémon and any of the above information is foreign to you, I am more than willing to chat with you about the semantic meaning of the above. For example, if you don't know the what a Pokemon's "type" is, or why that would be important.

If you find a bug in my PokeEntry class, please let me know. I threw it together last minute, so it's not very well tested.

*N.B.* you can be locked out from accessing the API if you make more than 100 requires for a particular Pokémon within 24 hours. After the 24 hour period ends, you will be unlocked.

If you want to understand more about where this data is coming from, you can check out [`https://pokeapi.co/`](https://pokeapi.co/).