import axios from "axios";

export const getPartDetails = () => {
    const URL = `http://localhost:8080/api/part-details/get`;
    return axios.get(URL).then(res => res);
}

export const addPartUnavailability = (unavailabilityData) => {
    const URL = `http://localhost:8080/api/part-details/add-part-unavailability`;
    return axios.post(URL, unavailabilityData).then(res => res);
}

export const addPart = (partDetails) => {
    const URL = `http://localhost:8080/api/part-details/add-part`;
    return axios.post(URL, partDetails).then(res => res);
}

export const updatePart = (partDetails) => {
    const URL = `http://localhost:8080/api/part-details/update-part`;
    return axios.post(URL, partDetails).then(res => res);
}