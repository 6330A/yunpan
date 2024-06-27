export const formatFileSize = (row) => {
    let bytes = 0;
    if (row.hasOwnProperty('fileSize')) {
        bytes = row.fileSize;
    } else {
        bytes = row;
    }
    if (bytes === 0) return '0B';
    const k = 1024;
    const sizes = [' B', ' KB', ' MB', ' GB', ' TB', ' PB', ' EB', ' ZB', ' YB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + sizes[i];
}

export const calculatePercent = (a, b) => {
    return parseFloat((100 * a / b).toFixed(2))
}