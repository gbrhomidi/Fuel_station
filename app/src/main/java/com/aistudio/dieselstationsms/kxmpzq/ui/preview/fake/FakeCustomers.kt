data class PreviewCustomer(

    val id: Long,

    val name: String,

    val phone: String

)

object FakeCustomers {

    val customers = listOf(

        PreviewCustomer(

            1,

            "شركة النفط",

            "777777777"

        ),

        PreviewCustomer(

            2,

            "محطة صنعاء",

            "711111111"

        ),

        PreviewCustomer(

            3,

            "شركة الجزيرة",

            "733333333"

        )

    )

}
