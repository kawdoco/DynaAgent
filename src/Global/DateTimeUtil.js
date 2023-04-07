
import moment from "moment-timezone";

export function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}

export function getLocalDate (datetime) {
    var utcDateTime = moment(datetime);
    return utcDateTime.tz("Asia/Colombo").format("YYYY-MM-DD HH:mm:ss");
  }