module.exports = class Validator {
  static isValidHighscore (highscore) {
    return typeof highscore === 'object' &&
      highscore !== null &&
      typeof highscore.name === 'string' &&
      typeof highscore.score === 'number' &&
      Validator.isValidDate(highscore.timestamp)
  }

  static isValidDate (d) {
    return !isNaN(new Date(d).getTime())
  }
}
