package thiengo.com.br.camisasdefutebol.domain

class Shirt(
    val soccerTeam: String,
    val imageId: Int,
    val level: Int,
    val size: String,
    val brand: String,
    val delivery: String,
    val price: Float,
    val amountBuy: Int ){

    /*
     * Permite a apresentação já formatada, para humanos,
     * dos dados de: level de camisa (1 ou 2); tamanho
     * da camisa; e marca da camisa.
     * */
    fun levelSizeBrandHumanFormat( uniform: String ) =
        String.format("%s %d - %s - %s", uniform, level, size, brand)

    /*
     * Permite a apresentação já formatada, para humanos,
     * dos dados de entrega: rótulo e valor.
     * */
    fun deliveryHumanFormat( freight: String ) =
        String.format("%s %s", freight, delivery)

    /*
     * Permite a apresentação já formatada, para humanos,
     * do dado de "quantidade em compra" da camisa atual.
     * */
    fun amountHumanFormat(unity: String, units: String): String{
        val unityLabel =
            if( amountBuy > 1)
                unity
            else
                units

        return String.format("%d %s - ", amountBuy, unityLabel)
    }
}