module 0x1::lambda_type_abilities {
    fun main(a: |&signer| has drop, b: |bool| has copy + drop, c: || u64 has store) {}
}
