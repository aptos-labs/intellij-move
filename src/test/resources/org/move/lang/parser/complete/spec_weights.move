module 0x1::spec_weights {
    spec fun pow(base: num, exp: num): num [weight = 20] {
        if (exp == 0) 1 else base * pow(base, exp - 1)
    }
    spec module {
        fun pow(base: num, exp: num): num [weight = 20] {
            if (exp == 0) 1 else base * pow(base, exp - 1)
        }
    }

    fun main() {}
    spec main {
        forall y: u64 [weight = 10]: y == y;
        forall y: u64 [weight = -10]: y == y;
        forall y: num {p(y)} [weight = 20]: y >= 0 ==> p(y);
    } proof {
        forall x: num, y: num [weight = 20] apply pow_mul_mono(base, x, y);
        forall x: num, y: num {pow(base, x), pow(base, y)} [weight = 20] apply pow_mul_mono(base, x, y);
    }


}
