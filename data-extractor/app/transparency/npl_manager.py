import spacy

_nlp = None

def get_nlp():
    global _nlp
    if _nlp is None:
        print("Loading spaCy model into worker memory...")

        _nlp = spacy.load("en_core_web_sm", disable=["parser", "lemmatizer", "ner"])

    return _nlp