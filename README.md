# Chisli

untuk menjalankan project menggunakan laptop/PC, pastikan memiliki

## requirements

- Java 21
- Vscode
- Extension JavaFX vscode

```sh
  cd src/chisli    # masuk ke dalam directory project
```

```sh
  code chisli      # buka vscode khusus untuk project (dengan root folder chisli)
```

lalu buka App.java

```
chisli/
└── src/main/java/chisli/App.java
```

di line atas 51

```java
    public static void main(String[] args) {    // line 51
        launch();
    }
```

akan muncul tombol Run
(jika tidak, pastikan sudah memenuhi requirements)

## Fitur

- Menghitung solusi permasalahan SPL, dengan metode:
  - Gauss
  - Gauss Jordan
  - Cramer
  - Inverse
- Menghitung solusi Interpolasi Polinomial
- Menghitung solusi Regresi Linier Berganda
- Menghitung solusi Regresi Linier Kuadratik
- Menghitung solusi Bicubic Spline Interpolation
- Meresize image
