import org.gradle.kotlin.dsl.project

/**
 * 账本相关配置。
 */
object CashGiftsConfig {
    /**
     * APP的名称。
     */
    const val name = "李氏账本"

    /**
     * 账本的描述，会展示在APP最下方。
     */
    const val description = "高考"

    /**
     * APP的标识符，用于区分不同的账本，避免应用ID重复导致不能同时安装多个账本。
     * 为空时使用默认应用ID。
     */
    const val qualifiedId = ""

    /**
     * 输入文件路径，相对于项目根目录([project.rootDir])。
     * 是一个Excel新版格式的文件（XLSX），目前需要四列——姓名、金额、地点、备注。
     * 其中金额应为数值型值，且会被保留整数。
     */
    const val xlsxFilePath = "cash.xlsx" // input file

    /**
     * 生成的csv文件路径。
     */
    const val csvDir = "build/generated/assets/"

    /**
     * 生成的csv文件名。
     *
     * 其实可以直接用APP处理xlsx文件的，这样做主要是想熟悉一下gradle的构建过程。
     */
    const val csvFileName = "cash.csv" // output file
}
