[33mcommit 7a4acdcd79eeaef3fe3930141de035735f650fff[m[33m ([m[1;36mHEAD[m[33m -> [m[1;32mmaster[m[33m)[m
Author: ph-level-14 <haaspeter2009@gmail.com>
Date:   Fri Sep 12 11:03:15 2025 +0200

    Creating loadQuizzes for loading the quizzes
    creating quizPath to get the Path of the quiz
    creating safe to get a safe name for the file

[1mdiff --git a/src/QuickStore.java b/src/QuickStore.java[m
[1mindex 3e3c850..e357b59 100644[m
[1m--- a/src/QuickStore.java[m
[1m+++ b/src/QuickStore.java[m
[36m@@ -30,6 +30,7 @@[m [mpublic class QuickStore {[m
             return names;[m
         }[m
     }[m
[32m+[m
     public static List<String>loadquizzes(String name) throws IOException {[m
         Path file = quizPath(name);[m
         if(!Files.exists(file)){[m
