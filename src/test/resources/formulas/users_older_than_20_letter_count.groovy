// Formula: Get users older than 20 and calculate total letters in their names
def now = java.time.LocalDate.now()
def twentyYearsAgo = now.minusYears(20)

// Get users born before 20 years ago (older than 20)
def olderUsers = userRepository.findUsersBornBefore(twentyYearsAgo)

// Calculate and return total letters in their names
olderUsers
    .parallelStream()
    .map { it.name }
    .mapToInt { name -> name.replaceAll("\\s+", "").length() }
    .sum()
