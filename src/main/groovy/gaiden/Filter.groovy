package gaiden

interface Filter {

    String before(String text)

    String after(String text)

    String afterTemplate(String text)
}
