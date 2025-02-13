static void main(String[] args) {
    def filePath = "C:\\blah\\blah\\testfile.txt"

    def flowFile = new File(filePath)
    def outputFile = new File("testOutput.txt") //writes to src folder of this project

    // Simulated session.write() with File I/O that is in nifi
    def simulatedSessionWrite = { file, callback ->
      file.withInputStream { inputStream ->
          outputFile.withOutputStream { outputStream ->
              callback.call(inputStream, outputStream)
          }
      }
    }

    def headerLine = true

    if (flowFile.exists()) {
    simulatedSessionWrite(flowFile, { inputStream, outputStream ->
        sep = '|'

        // this is the part that mimicks what we'd normally put in inputStream.eachLine(StandardCharsets.UTF_8.name()) { line, lineNo ->
        def modifiedText = inputStream.text.readLines().collect { line ->
            if (headerLine){
                corrected ="HeaderRecord${sep}${line}"
                headerLine = false
                corrected //mimicks writer.write(corrected + '\n')

            } else {
                corrected = "LineRecord${sep}${line}"
                corrected //mimicks writer.write(corrected + '\n')
            }

            }.join("\n")

            outputStream.write(modifiedText.getBytes("UTF-8"))
        })

        println "Modified file content:\n${outputFile.text}"

  } else {
    println "File not found!"
  }
}