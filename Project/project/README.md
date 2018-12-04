# KDM - Lab Assignment 4

## DJ Yuhn

CS5560 Knowledge Discovery Management - Lab Assignment 4



# Introduction

For CS5560 Knowledge Discovery Management, Lab Assignment 4 will be working on 100 abstracts I have found to be used as data sets for my team's project on Mental Illness. The abstracts were retrieved under the search of `mental+illness+treatment` from NCBI's PubMed.

The project's goal is to construct a Knowledge Graph for Mental Illness. These 100 abstracts, for this assignment, will undergo Spark processing with basic Natural Language Processing (NLP) such as lemmatization and stop words removed. The top 20 Term Frequency-Inverse Document Frequency (TF-IDF) words will be found with their associated Word2Vec vectors containing synonyms for each TF-IDF word.

These TF-IDF words will serve as the schema for the ontology construction. The triplets from the abstracts are extracted and separated into subjects, predicts, and objects. The subjects and objects will be classified under the TF-IDF words on the basis of partial matching. That is, if the TF-IDF word is contained within the subject or predicate then it shall be classified under that category. If the TF-IDF word is not found, then the synonyms for the TF-IDF word are iterated through to see if the subject or object contains a partial match on one of its synonyms. If so, then the subject or object is classified under the TF-IDF word. If not, the subject or object is then classified under the default of "Other".

Once the subjects and objects have been classified, the ontology is constructed.



## Tasks

For the 100 abstracts relevant to the project topic, Mental Illness, perform the following triplet characterizations:

1. InverseOf
2. Symmetric Property
3. Transitive Property
4. Property Chain Axiom
5. Asymmetric Property
6. Irreflexive Property



# Data and Data Stats

## Entities

| Subjects                                            | Objects                                              |
| --------------------------------------------------- | ---------------------------------------------------- |
| long-term health-related quality                    | treatment of choice for generalised anxiety disorder |
| treatment utilization                               | cognitive behavior like learning                     |
| resting-state functional magnetic resonance imaging | recent-onset schizophrenia                           |
| anxiety disorder                                    | lower incidence of relapse                           |
| eating-disorder psychopathology                     | mental/cognitive disability arise                    |

These are some of the entities found from the 100 abstracts after performing lemmatization and removing the stop words. To obtain more concise and accurate results, further filtering may need to be applied.

## Predicates

| Predicates            |
| --------------------- |
| will discuss          |
| further will describe |
| suffer from           |
| have increase over    |
| first examine         |

Several examples of predicates found after performing the lemmatization and removing stop words.

## Triplets

### Triplet Examples

| Triplets             |              |                                                |
| -------------------- | ------------ | ---------------------------------------------- |
| dopaminergic agonist | be           | related                                        |
| dopaminergic agonist | be           | related                                        |
| female               | report       | higher rate of anemia                          |
| fasting              | influence    | fear memory formation                          |
| early intervention   | may decrease | financial burned associate with mental illness |

### Characterizations

Triplets extracted from the abstracts can be characterized by the following properties: InverseOf, Symmetric, Transitive, PropertyChainAxiom, Asymmetric, and Irreflexive. Using the lecture from the University of Jyväskylä, these characteristics are explained and found for the triplets I have extracted.

#### InverseOf

> { ?P owl:inverseOf ?Q . ?S ?P ?O } => { ?O ?Q ?S } 

| Triplet                                                      | InverseOf                                             |
| ------------------------------------------------------------ | ----------------------------------------------------- |
| they    cope with  mental/cognitive disability arise         | it  have for   they                                   |
| functional disability   related to cognition from that attribute to motor symptom | it  be seventh lead cause worldwide for    disability |

The inverseOf implementation is provided in the screenshot below. From all of the triplets in the abstracts, the above are the only ones found using the `contains()` method instead of the `equals()` method. The reason for the triplet's respective InverseOf is due to 'it' being contained within the object of the triplet: cogn**it**ive and cogn**it**ion.

![InverseOf_Code](../docs/Lab4/InverseOf_Code.png)

#### Symmetric

> { ?P rdf:type owl:SymmetricProperty . ?S ?P ?O } => { ?O ?P ?S } 

From the triplets extracted from the 100 abstracts, no triplet met the conditions to satisfy the symmetric property. That is, each triplet's subject and object was not found to be swapped in another triplet where the subject was then the object, and the object was then the subject.

![Symmetric_Code](../docs/Lab4/Symmetric_Code.png)



#### Transitive

> { ?P rdf:type owl:TransitiveProperty . ?S ?P ?X. ?X ?P ?O } => { ?S ?P ?O }

| Triplet                                                      | Triplet2                                                     | Transitive Triplet                                           |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| cognitive-behavioural therapy   be treatment of choice for generalised anxiety disorder | apomorphine be non-specific                                  | cognitive-behavioural therapy   be non-specific              |
| cognitive deficit   be in  patient with mood disorder        | antipsychotic polypharmacy  be in  schizophrenia treatment   | cognitive deficit   be in  schizophrenia treatment           |
| antipsychotic polypharmacy  be in  schizophrenia treatment   | approve thiol-containing redox modulatory compound  be in  trial for many neurological disorder | antipsychotic polypharmacy  be in  trial for many neurological disorder |
| patient be with    first episode of psychosis                | other chronic psychiatric disorder  be with    onset at youth | patient be with    onset at youth                            |
| gray matter volume  be in  patient with functional movement disorder | approve thiol-containing redox modulatory compound  be in  trial for many neurological disorder | gray matter volume  be in  trial for many neurological disorder |

Under the transitive property, the predicate of a triplet determines the connection. That is, the predicate is responsible for creating the connection from the first triplet's subject to the second triplet's object. In the triplets extracted this resulted in finding associations that followed the transitive property but produced many transitive triplets that conveyed confusing meanings.

![Transitive_Code](../docs/Lab4/Transitive_Code.png)



#### Property Chain Axiom

> If the property P1 relates individual A1 to individual A2, and property P2 relates individual A2 to individual An, then property P relates individual A1 to individual An.

| Triplet                                                      | Triplet2                                                     | Property Chain Triplet                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| improving communication may alleviate  disorder              | disorder    be in  user ' naturalistic environment           | improving communication --NEW PREDICATE--  user ' naturalistic environment |
| its use have particularly well document in Parkinson 's disease | Parkinson 's disease    be commonly associate with motor symptom | its use --NEW PREDICATE--  motor symptom                     |
| biomarker assessment methodology    vary substantially between study | study   examine    efficacy of repetitive transcranial magnetic stimulation in sample of young people aged | biomarker assessment methodology    --NEW PREDICATE--  efficacy of repetitive transcranial magnetic stimulation in sample of young people aged |

Under the property chain axiom, the subject of triplet T1 is associated with the object of another triplet T2 only if T1's object is the subject of T2. A new predicate is used to describe this association between the subject and the object. Defining this new predicate automatically would require assessing the context and conveyed meaning of each triplet and automatically generating a predicate that would make sense given the new subject and object. For my use, a stand-in of '--NEW PREDICATE-- was used as defining a method of automatically generating a predicate is beyond the scope of this project.

 ![PropertyChainAxiom_Code](../docs/Lab4/PropertyChainAxiom_Code.png)



#### Asymmetric

> Some of the property characteristics set certain conditions  and allow reasoners to detect inconsistency of the ontology.
>
> John :isChildOf Mary
> Mary :isChildOf John

The asymmetric property follows the exact implementation for finding symmetric triplets. Unlike in the symmetric property, asymmetric is concerned with whether or not the findings are consistent and logical. In the above quote, the symmetric triplet that is 'Mary is child of John' is inconsistent as Mary cannot be a child of John if John is already the child of Mary. Since the asymmetric property follows the same implementation as the symmetric property, none of the triplets extracted from my dataset satisfy the conditions for the asymmetric property.

![Asymmetric_Code](../docs/Lab4/Asymmetric_Code.png)



#### Irreflexive

> Some of the property characteristics set certain conditions  and allow reasoners to detect inconsistency of the ontology.
>
> Mary :motherOf John
> Mary :motherOf Mary

The irreflexive property, like the asymmetric property, is concerned with whether or not the findings are consistent and logical. The irreflexive property reasons that the subject of a triplet cannot also be the object. In the above case, Mary is the mother of John, but she cannot also be her own mother. Similarly with the asymmetric property, none of the triplets extracted from my dataset satisfy the conditions for the irreflexive property.



## TF-IDF Terms / Classes

| TF-IDF Terms                    | Synonyms                                                     |
| ------------------------------- | ------------------------------------------------------------ |
| fear                            | fasting, impairment, magnetic, addiction, somatic, schizophrenia, |
| lithium                         | ADHD, treatment, anxiety, mental, literature, disorders      |
| ECT (Electroconvulsive Therapy) | oxidative, mechanisms, assessment, childhood, research       |
| apomorphine                     | treatment, addiction, oxidative, biomarkers, negative        |
| psychotropics                   | disorder, treatment, limited, illness                        |

Several examples of some TF-IDF words used as classes for the ontology. Some of the TF-IDF words obtained are not particularly meaningful and were removed as class types for the ontology construction. However, this effects the classifying of the entities as the total number of possible classes has been decreased. This results in some entities having to be assigned a default class titled "Other".

In total, 47 classes were found from these TF-IDFs and 642 individuals were constructed.

## Data

More information related to the findings of this data can be located at https://www.box.com/home under my profile for Lab4.



## Ontology

![MentalHealthTreatment_CustomClassOntology](../docs/Lab3/MentalHealthTreatment_CustomClassOntology.PNG)

The image above displays the current ontology of the Mental Health Treatment aspect for the team's Mental Illness project. This ontology was constructed using the TF-IDF words and disregarded the medical words obtained from BioNLP for classification.

![MentalHealthTreatment_CustomClassOntology_MultipleClassType](../docs/Lab3/MentalHealthTreatment_CustomClassOntology_MultipleClassType.PNG)

The image above illustrates a case of a class being assigned as a part of another class. That is, 'addiction' falls under both 'fear' and 'fasting' while also being its own class. This image also shows the case of the class 'fear' being in itself a class of 'fear'. That is, 'fear' is also an individual while also being a class.

# Source Code

The source code for this Lab Assignment was provided by the class instructor Mayanka ChandraShekar: [mckw9@mail.umkc.edu](mckw9@mail.umkc.edu) from previous ICPs



# Screenshots of Code

![TFIDF_Word2Vec](../docs/Lab3/TFIDF_Word2Vec.png)

![PartialMatchingTFIDF](../docs/Lab3/PartialMatchingTFIDF.png)

![OntologyConstruction](../docs/Lab3/OntologyConstruction.png)

# References

Khriyenko Oleksiy (2018). Lecture 4: Reasoning. TIES4520 Semantic Technologies for Developers Autumn 2018 [PowerPoint slides]. Retrieved from https://umkc.app.box.com/s/a5u9wtlybzcnerehbm3zs0r1xm3nbvt8/file/348251029402.