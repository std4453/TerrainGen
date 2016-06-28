# TerrainGen

TerrainGen is a playground of mine created when I read bunches of documents about procedural random terrain generation and finally decided to implement the algorithms by myself using *Java*.

In fact, this repository (rather than a project) doesn't have an accurate aim, what shall I do, what use should it be of, etc.. I just wanted to train my Java and algorithm skills, as well as publishing it on *Github* open-source so that everyone can give me advices and take advantage of my work, which is definitely welcomed.

---

# 1. Algorithms

Though the repository will finally contain many different PRTG algorithms, I shall start everything from an algorighm decribed in [this post][algorithm-1]:

[algorithm-1]: http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/

While during the development / implementation of the algorithm, I'll try and implement some other algorithms oftenly-used in PRG, like *Perlin Noise* or *Veronoi Algorithm*, etc..

Still this algorithm is merely an simple one:
  
* It only generates terrain for a rather small area;
* It works only on a square map;
* It cannot be expanded to theoretically infinite size;
* It doesn't contain detailed topographic features (canyons, caves, etc.);

Though it would become a fairly good start. 
 
These are algorithms that I want to implement after that:

1. Voxel / Pixel-based terrain (like *Minecraft* or *Terraria*)
* Rogue-like game terrain (like *Dwarf Fortress*)
* Real-time strategy terrain (like *Starcraft* or *Age of Empires*)

# 2. Structure

The whole program is based on simple structure with its front-end and back-end seperated.

Readably, the packages in this Java project forms a tree:

* Front-End
	* Basic UI System
		* Parameter Controls
		* General UI Templates
		* User Input Manager (may be replaced by Swing built-in ones) 
	* 3D System
		* Modelling Adaptors / Interfaces
		* Navigation Controls
		* Renderer
		* First-Person Exploration System
			* TO BE DEVELOPED...
* Back-End
	* Utilities
	* Common Algorithms
	* PRTG Algorithms
		* 1
		* 2
		* ...
	* Parameter-Driven Adaptors / Interfaces
	* Multi-Layer Generation Model
* Communication
	* Event Stream Model
		* Async / Multithread Model
		* Dispacher & Receiver
		* Producer & Consumer
		* Blob Data Pipe
	* Buffers

Basically, the backend part works like an library: It receives parameters from the front-end / caller of the library, generates data (expected to work asynchronously), and passes data through the *Event Stream Model*. While the front-end part works like an framework: It initializes everything and waits for the modules to construct the data needed.  
In this way, the whole repository of different uses:

* library that generates random terrain data
* visible tools for random-generated terrain graph
* first-person explorer in randomly generated 3D world

# 3.Conclusion

Believe it or not, I've not yet figured out what to write here, so...

This project is still under pre-alpha development, and you won't expect anything to work properly yet.   
**Please be patient, I'm a student with little free time..**