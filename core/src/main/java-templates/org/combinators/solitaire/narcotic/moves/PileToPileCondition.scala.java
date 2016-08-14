if (!target.empty() && target.rank() == card.getRank()) {
    String pile1Name = target.getName();
    String pile2Name = source.getName();

    int rc = pile1Name.compareTo (pile2Name);

     // We must be to the left
    if (rc < 0) {
        validation = true;
    }
}