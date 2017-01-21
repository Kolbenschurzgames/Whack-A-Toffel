module.exports = (function () {
  const isValidHighscore = function (highscore) {
    return typeof highscore === 'object' &&
      highscore !== null &&
      typeof highscore.name === 'string' &&
      typeof highscore.score === 'number' &&
      isValidDate(highscore.timestamp)
  }

  const isValidDate = function (d) {
    return !isNaN(new Date(d).getTime())
  }

  return {
    isValidHighscore: isValidHighscore,
    isValidDate: isValidDate
  }
})()
