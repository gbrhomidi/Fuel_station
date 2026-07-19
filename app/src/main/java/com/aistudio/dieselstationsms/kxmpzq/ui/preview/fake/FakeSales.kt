data class PreviewSale(

    val invoiceNo: String,

    val customer: String,

    val total: String

)

object FakeSales {

    val invoices = listOf(

        PreviewSale(

            "INV-001",

            "شركة النفط",

            "15,000"

        ),

        PreviewSale(

            "INV-002",

            "محطة تعز",

            "8,500"

        )

    )

}
