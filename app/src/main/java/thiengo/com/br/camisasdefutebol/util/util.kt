package thiengo.com.br.camisasdefutebol.util

/*
 * Implementação simples do algoritmo de Luhn, algoritmo
 * utilizado para validação de números de cartão de
 * crédito. Mais sobre o algoritmo de Luhn nos links a
 * seguir:
 *
 * https://en.wikipedia.org/wiki/Luhn_algorithm
 * https://gist.github.com/mdp/9691528
 * */
fun luhnAlgorithm( creditCardNumber: String ): Boolean{
    var sum = 0
    var alternate = false

    for (i in creditCardNumber.length - 1 downTo 0) {

        var n = Integer.parseInt( creditCardNumber.substring(i, i + 1) )

        if (alternate) {
            n *= 2
            if (n > 9) {
                n = n % 10 + 1
            }
        }
        sum += n
        alternate = !alternate
    }

    return sum % 10 == 0
}