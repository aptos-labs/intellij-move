module 0x1::proofs_and_lemmas {
    spec main {

    } proof {
        assume [trusted] true;
    }

    spec module {
        lemma lemma_with_proof() {} proof { assume [trusted] true; }
    }
}
