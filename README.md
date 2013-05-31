nophonetic
==========

Fonetisk søk på norsk i Solr. Advarsel: Eksperimentell kode.


Bygging
-------

	git clone git://github.com/kristoffer-dyrkorn/nophonetic.git
	cd nophonetic
	mvn clean package


Beskrivelse
-----------

Nophonetic gjør det mulig å støtte fonetisk søk på norsk i søkemotoren Solr (http://lucene.apache.org/solr/).
Solr støtter diverse algoritmer for fonetisk søk, men disse er utviklet for andre språk enn norsk.

Framgangsmåten i denne løsningen er å bruke en tabell med kodingsregler spesialutviklet for norsk. Olaf Havnes laget denne, se http://www.havnes.com/Downloads/PhoneticEncoder.shtml. Kodingsreglene fører til at ord som høres like ut blir kodet om til tekststrenger som er like. Dermed vil en vanlig "eksakt sammenligning" i en søkemotor, når dette kjøres mot kodede ord, fungere som en "fonetisk sammenligning".

En forenklet utgave av klassen GenericTransformator - fra biblioteket Jazzy, http://jazzy.sourceforge.net/, brukes for å lese inn og lagre kodingsregler. GenericTransformer er utviklet av Robert Gustavsson. Til slutt er dette bygget inn i et TokenFilterFactory (fra Solrs API).

NB Kodingsreglene er ikke grundig testet. Imidlertid antas de å gi bedre resultater enn eksisterende (algoritmiske) kodere.


Installasjon
------------

* Etter bygging, legg inn nophonetic.jar i en katalog lib/ under rot-katalogen for Solr-kjernen
* Legg inn phonetic.dat i katalogen conf/ under rot-katalogen for Solr-kjernen
* Legg inn en egen fieldType i schema.xml slik at transformasjoner kjøres både under søk og indeksering, f.eks slik:

	<fieldType name="phonetic_type_no" class="solr.TextField" positionIncrementGap="100">
	  <analyzer>
	    <tokenizer class="solr.StandardTokenizerFactory"/>
	    <filter class="no.bekk.bekkopen.tokenfilter.TransformationFilterFactory" />
	  </analyzer>
	</fieldType>

* Legg inn et felt som bruker denne fieldType'n, f.eks slik:

	<field name="phonetic_text" type="phonetic_no" indexed="true" stored="false"/>


Gjenstår
--------

* Støtte Solr 4.3.0
* Måle ytelsen på transformasjoner og evt forbedre
