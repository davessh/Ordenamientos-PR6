package logica;

public class Ordenamientos {

    public static void quickSort(String[] array) {
        if (array == null || array.length <= 1) return;
        quickSortString(array, 0, array.length - 1);
    }

    private static void quickSortString(String[] array, int first, int last) {
        if (first >= last) return;

        int left = first;
        int right = last;
        String pivot = array[(first + last) / 2];

        while (left <= right) {
            while (array[left].compareTo(pivot) < 0) left++;
            while (array[right].compareTo(pivot) > 0) right--;

            if (left <= right) {
                if (left != right) {
                    String temp = array[left];
                    array[left] = array[right];
                    array[right] = temp;
                }
                left++;
                right--;
            }
        }

        if (first < right) quickSortString(array, first, right);
        if (left < last) quickSortString(array, left, last);
    }

    public static void insercion(String[] a, int n) {
        for (int i = 1; i < n; i++) {
            String aux = a[i];
            int k = i - 1;

            while (k >= 0 && aux.compareTo(a[k]) < 0) {
                a[k + 1] = a[k];
                k--;
            }
            a[k + 1] = aux;
        }
    }

    public static void selection(String[] a, int n) {
        for (int i = 0; i < n - 1; i++) {
            String menor = a[i];
            int k = i;

            for (int j = i + 1; j < n; j++) {
                if (a[j].compareTo(menor) < 0) {
                    menor = a[j];
                    k = j;
                }
            }

            a[k] = a[i];
            a[i] = menor;
        }
    }

    public static void mergeSort(String[] datos) {
        if (datos == null || datos.length <= 1) return;

        String[] temp = new String[datos.length];
        mergeSortRec(datos, temp, 0, datos.length - 1);
    }

    private static void mergeSortRec(String[] datos, String[] temp, int izq, int der) {
        if (izq >= der) return;

        int medio = (izq + der) / 2;

        mergeSortRec(datos, temp, izq, medio);
        mergeSortRec(datos, temp, medio + 1, der);

        merge(datos, temp, izq, medio, der);
    }

    private static void merge(String[] datos, String[] temp, int izq, int medio, int der) {
        int i = izq;
        int j = medio + 1;
        int k = izq;

        while (i <= medio && j <= der) {
            if (datos[i].compareTo(datos[j]) <= 0) {
                temp[k++] = datos[i++];
            } else {
                temp[k++] = datos[j++];
            }
        }

        while (i <= medio) temp[k++] = datos[i++];
        while (j <= der) temp[k++] = datos[j++];

        for (i = izq; i <= der; i++) datos[i] = temp[i];
    }

    public static void shell(String[] a, int n) {
        int intervalo = n;
        while (intervalo > 1) {
            intervalo /= 2;
            boolean flag = true;

            while (flag) {
                flag = false;
                int indice = 0;

                while ((indice + intervalo) < n) {
                    if (a[indice].compareTo(a[indice + intervalo]) > 0) {
                        String aux = a[indice];
                        a[indice] = a[indice + intervalo];
                        a[indice + intervalo] = aux;
                        flag = true;
                    }
                    indice++;
                }
            }
        }
    }

    public static void radixSortStrings(String[] arr) {
        final int RADIX = 256;

        int maxLen = 0;
        for (String s : arr) {
            if (s.length() > maxLen) maxLen = s.length();
        }

        String[] aux = new String[arr.length];

        for (int pos = maxLen - 1; pos >= 0; pos--) {
            int[] count = new int[RADIX + 1];

            for (String s : arr) {
                int charIndex = (pos < s.length()) ? s.charAt(pos) : 0;
                count[charIndex + 1]++;
            }

            for (int i = 0; i < RADIX; i++) count[i + 1] += count[i];

            for (String s : arr) {
                int charIndex = (pos < s.length()) ? s.charAt(pos) : 0;
                aux[count[charIndex]++] = s;
            }

            System.arraycopy(aux, 0, arr, 0, arr.length);
        }
    }


//métodos para double
    public static void quickSort(double[] array) {
        if (array == null || array.length <= 1) return;
        quickSortDouble(array, 0, array.length - 1);
    }

    private static void quickSortDouble(double[] array, int first, int last) {
        if (first >= last) return;

        int left = first;
        int right = last;
        double pivot = array[(first + last) / 2];

        while (left <= right) {
            while (array[left] < pivot) left++;
            while (array[right] > pivot) right--;

            if (left <= right) {
                double temp = array[left];
                array[left] = array[right];
                array[right] = temp;
                left++;
                right--;
            }
        }

        if (first < right) quickSortDouble(array, first, right);
        if (left < last) quickSortDouble(array, left, last);
    }

    public static void insercion(double[] a, int n) {
        for (int i = 1; i < n; i++) {
            double aux = a[i];
            int k = i - 1;

            while (k >= 0 && aux < a[k]) {
                a[k + 1] = a[k];
                k--;
            }
            a[k + 1] = aux;
        }
    }

    public static void selection(double[] a, int n) {
        for (int i = 0; i < n - 1; i++) {
            double menor = a[i];
            int k = i;

            for (int j = i + 1; j < n; j++) {
                if (a[j] < menor) {
                    menor = a[j];
                    k = j;
                }
            }

            a[k] = a[i];
            a[i] = menor;
        }
    }

    public static void mergeSort(double[] datos) {
        if (datos == null || datos.length <= 1) return;

        double[] temp = new double[datos.length];
        mergeSortRec(datos, temp, 0, datos.length - 1);
    }

    private static void mergeSortRec(double[] datos, double[] temp, int izq, int der) {
        if (izq >= der) return;

        int medio = (izq + der) / 2;

        mergeSortRec(datos, temp, izq, medio);
        mergeSortRec(datos, temp, medio + 1, der);

        merge(datos, temp, izq, medio, der);
    }

    private static void merge(double[] datos, double[] temp, int izq, int medio, int der) {
        int i = izq;
        int j = medio + 1;
        int k = izq;

        while (i <= medio && j <= der) {
            temp[k++] = (datos[i] <= datos[j]) ? datos[i++] : datos[j++];
        }

        while (i <= medio) temp[k++] = datos[i++];
        while (j <= der) temp[k++] = datos[j++];

        for (i = izq; i <= der; i++) datos[i] = temp[i];
    }

    public static void shell(double[] a, int n) {
        int intervalo = n;
        while (intervalo > 1) {
            intervalo /= 2;
            boolean flag = true;

            while (flag) {
                flag = false;
                int indice = 0;

                while ((indice + intervalo) < n) {
                    if (a[indice] > a[indice + intervalo]) {
                        double aux = a[indice];
                        a[indice] = a[indice + intervalo];
                        a[indice + intervalo] = aux;
                        flag = true;
                    }
                    indice++;
                }
            }
        }
    }
}
