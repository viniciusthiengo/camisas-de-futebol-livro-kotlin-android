package thiengo.com.br.camisasdefutebol.data

import thiengo.com.br.camisasdefutebol.R
import thiengo.com.br.camisasdefutebol.domain.Shirt

class Database {
    companion object {
        fun getShirts()
            = listOf(
                Shirt(
                    "Dínamo de Zagreb",
                    R.drawable.dinamo_zagreb,
                    2,
                    "P",
                    "Adidas",
                    "grátis",
                    279.00F,
                    1
                ),
                Shirt(
                    "Borussia Dortmond",
                    R.drawable.borussia_dortmond,
                    1,
                    "M",
                    "Puma",
                    "grátis",
                    259.00F,
                    1
                ),
                Shirt(
                    "Atlanta United",
                    R.drawable.atlanta_united,
                    1,
                    "G",
                    "Adidas",
                    "grátis",
                    179.00F,
                    1
                ),
                Shirt(
                    "América do México",
                    R.drawable.america_mexico,
                    1,
                    "G",
                    "Nike",
                    "grátis",
                    279.00F,
                    1
                ),
                Shirt(
                    "Guangzhou Evergrande",
                    R.drawable.guangzhou_evergrande,
                    1,
                    "GG",
                    "Nike",
                    "grátis",
                    279.00F,
                    1
                ),
                Shirt(
                    "Zenit",
                    R.drawable.zenit,
                    2,
                    "M",
                    "Nike",
                    "grátis",
                    179.00F,
                    1
                )
        )
    }
}