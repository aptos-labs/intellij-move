module 0x1::match_with_ints {
    fun match_with_ints(code: u16): vector<u8> {
        match (code) {
            200 => b"OK",
            404 => b"Not Found",
            _   => b"Unknown",
        }
    }

    fun match_with_ranges(x: i32): u8 {
        match (x) {
            ..0  => 0,   // negative
            0    => 1,   // zero
            1..  => 2,   // positive
        }
    }

    fun ref_match(p: &Pair): u64 {
        match (p) {
            Pair::P(x, 2) => *x + 100,  // x: &u64
            _             => 0,
        }
    }
}
