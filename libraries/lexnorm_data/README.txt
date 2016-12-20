RESOURCE: LEXICAL NORMALISATION ANNOTATIONS FOR SHORT TEXT MESSAGES

VERSION: 1.2
LAST UPDATE: June 26, 2013


DESCRIPTION: 
This is an updated version of the dataset used for lexical normalisation described in:

  Bo Han and Timothy Baldwin (2011) Lexical normalisation of short text
  messages: Makn sens a #twitter. In Proceedings of the 49th Annual Meeting of
  the Association for Computational Linguistics, Portland, USA.

The updates are detailed below, and were generously provided by Jacob
Eisenstein, as part of the following paper:

  Yi Yang and Jacob Eisenstein (2013) A Log-Linear Model for Unsupervised Text
  Normalization. In Proceedings of Conference on Empirical Methods in Natural
  Language Processing (EMNLP), Seattle, USA.


The dataset contains 549 English messages sampled from the Twitter API (from
August to October, 2010), comprising 1184 normalised tokens, annotated as
follows:

* Corrections are necessarily one-to-one token mappings (e.g. u -> you),
  ignoring one-to-many mappings (e.g. ttyl -> talk to you later).

* Determination of what tokens are out of vocabulary is based on the GNU
  Aspell dictionary (version 0.60.6 with minor modifications, as detailed in
  the paper), meaning normalisation of in-vocabulary tokens (e.g. wit -> with)
  is out of scope.

* Ill-formed words must consist of letters, digits, hyphens (-) and single
  quotes (') only. Hashtags, mentions and urls are not candidates for
  normalisation.

* In the case that there is a one-to-one normalisation for a token onto an IV
  word *only* via a lower-register contraction (e.g. gonna, wanna), we allow this.

* If it was not possible to normalise with high confidence, the token was left
  untouched.

The data format is:

<Message Token Number>
<input 1>	<v 1>		<norm 1>
<input 2>	<v 2>		<norm 2>
......

meaning that each message begins with a message token number, followed a single token
per line, with a flag indicating whether it is:

  OOV (out of vocabulary)
  IV (in vocabulary)
  NO (symbol, or Twitter-specific token; not a candidate for normalisation)

(recalling that only OOV tokens are candidates for normalisation), and the
canonical token of the token; all files are tab separated. If the token is
ill-formed, the normalised token is that given by the annotator, otherwise it
is simply copied to the normalisation part. Here is an sample file fragment:

4
new	IV	new
pix	OOV	pictures
comming	OOV	coming
tomoroe	OOV	tomorrow

The above tweet has 4 tokens, the first token is IV, and the normalised token
is thus identical to the original. The last three tokens are OOV, and have
been judged to be ill-formed and normalised appropriately.


CHANGELOG:

v1.1 Corrected OOV flag
v1.2 Corrections to annotations from Jacob Eisenstein


CHANGES (by Jacob Eisenstein)

The corrections are based on a desire to maintain annotation
consistency across the entire dataset; others are based on my
understanding of Twitter writing conventions and my best guess of the
author's intention. I maintained the premise of only normalizing OOV
words. I made the following classes of corrections, which are
described with representative examples:

* correct erroneous normalizations
effin --> fucking (was effin; "fucking" is IV)
ohhhh --> oh
bball --> basketball (was ball)
fess --> fess (when followed by "up", because "confess up" doesn't work)
finna --> finna (not finally)
nigga --> nigga (was nigger)
yeahh --> yeah (not yes; "yeah" is IV)
btw --> between (except when it means "by the way")
bb --> blackberry (not baby, depending on context)
smh --> smh (not somehow)

* make existing normalizations more consistent
2 --> to/too, unless number is intended
4 --> for, unless number is intended
aint --> ain't (consistent)
yall --> y'all, not you (consistent)
haha(ha)* --> haha

* if the normalization is not obvious from context, leave it.
twords --> twords (not towards)

* do not normalize multiwords 
outta --> outta (not "out", means "out of")
ima --> ima (not i'll)
coulda --> coulda (not could)



LICENSE:

This dataset is made available under the terms of the Creative Commons
Attribution 3.0 Unported licence
(http://creativecommons.org/licenses/by/3.0/), with attribution via citation
of the following papers:

  Bo Han and Timothy Baldwin (2011) Lexical normalisation of short text
  messages: Makn sens a #twitter. In Proceedings of the 49th Annual Meeting of
  the Association for Computational Linguistics, Portland, USA.

  Yi Yang and Jacob Eisenstein (2013) A Log-Linear Model for Unsupervised Text
  Normalization. In Proceedings of Conference on Empirical Methods in Natural
  Language Processing (EMNLP), Seattle, USA.


DISCLAIMER:

The dataset may contain offensive messages. They do not necessarily represent
the views, policies or opinions of the authors or The University of Melbourne.
The distribution of this data in no way indicates claim of ownership over the
original data.


ACKNOWLEDGEMENTS:

Many thanks to Marco Lui and Li Wang for their original annotation efforts.


CONTACTS:

Any comments or suggestions on the dataset are appreciated:

  Bo HAN (hanb@student.unimelb.edu.au) 
  Tim Baldwin (tb@ldwin.net)
