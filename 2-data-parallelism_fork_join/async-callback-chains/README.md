
- [Thread.Sleep(n) means block the current thread for at least the number of timeslices](https://stackoverflow.com/a/8815944/432903)

Description

- each pickup-task takes = 5 secs
- number of pickup tasks = 2

- each packup-task takes = 5 secs
- number of packup tasks = 2

total time - 20 secs

```
$ sbt compile

$ sbt "runMain SequentialNonblockingTasks"
[info] Running SequentialNonblockingTasks
[scala-execution-context-global-57]-picking item1

[scala-execution-context-global-65]-picking item2

[scala-execution-context-global-57]-packing item1

[scala-execution-context-global-65]-packing item2

[scala-execution-context-global-65]-packing and packing done
[success] Total time: 21 s, completed Apr 1, 2018 5:57:44 PM
```

fj pool
-------

```
$ sbt "runMain WorkStealingOrderShippingTask"
[info] Loading global plugins from /Users/a1353612/.sbt/0.13/plugins
[info] Loading project definition from /Users/a1353612/buybest/sc212/parallel-programming/2-data-parallelism_fork_join/parallel-chain/project
[info] Set current project to parallel-chain (in build file:/Users/a1353612/buybest/sc212/parallel-programming/2-data-parallelism_fork_join/parallel-chain/)
[info] Running WorkStealingOrderShippingTask
#[ForkJoinPool-1-worker-57] - creating sub-tasks for [0-50] and [50-100]
#[run-main-0] - shutting down executor pool
#[ForkJoinPool-1-worker-57] - creating sub-tasks for [0-25] and [25-50]
#[ForkJoinPool-1-worker-114] - creating sub-tasks for [0-25] and [25-50]
#[ForkJoinPool-1-worker-57] - creating sub-tasks for [0-12] and [12-25]
#[ForkJoinPool-1-worker-43] - creating sub-tasks for [0-12] and [12-25]
#[ForkJoinPool-1-worker-114] - creating sub-tasks for [0-12] and [12-25]
#[ForkJoinPool-1-worker-100] - creating sub-tasks for [0-12] and [12-25]
#[ForkJoinPool-1-worker-57] - creating sub-tasks for [0-6] and [6-12]
#[ForkJoinPool-1-worker-114] - creating sub-tasks for [0-6] and [6-12]
#[ForkJoinPool-1-worker-29] - creating sub-tasks for [0-6] and [6-13]
#[ForkJoinPool-1-worker-15] - creating sub-tasks for [0-6] and [6-13]
#[ForkJoinPool-1-worker-100] - creating sub-tasks for [0-6] and [6-12]
#[ForkJoinPool-1-worker-43] - creating sub-tasks for [0-6] and [6-12]
#[ForkJoinPool-1-worker-72] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-29] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-58] - creating sub-tasks for [0-3] and [3-7]
#[ForkJoinPool-1-worker-114] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-30] - creating sub-tasks for [0-3] and [3-7]
#[ForkJoinPool-1-worker-44] - creating sub-tasks for [0-6] and [6-13]
#[ForkJoinPool-1-worker-100] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-73] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-57] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-15] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-44] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-43] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-86] - creating sub-tasks for [0-6] and [6-13]
#[ForkJoinPool-1-worker-87] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-60] - creating sub-tasks for [0-3] and [3-7]
#[ForkJoinPool-1-worker-86] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-101] - creating sub-tasks for [0-3] and [3-6]
#[ForkJoinPool-1-worker-102] - creating sub-tasks for [0-3] and [3-7]
[ForkJoinPool-1-worker-72] - shipping 840390d9-e925-4e7d-a8af-c4e5dbce71bb
[ForkJoinPool-1-worker-1] - shipping beca0f3a-2bad-4290-b2ed-ed6b9ab9a818
[ForkJoinPool-1-worker-15] - shipping 5bee8797-963d-4e2b-8dc7-dd6eb766f6f2
[ForkJoinPool-1-worker-44] - shipping 9f3575e1-352f-4014-89f4-68f474185ac7
[ForkJoinPool-1-worker-58] - shipping 1ff657bd-3b30-4bc2-92aa-46f16af8fa0d
[ForkJoinPool-1-worker-116] - shipping c940152f-a471-4a37-91d5-a186c43f32a8
[ForkJoinPool-1-worker-2] - shipping be326787-bd9d-4d5a-acc2-93b82af55766
[ForkJoinPool-1-worker-30] - shipping c6c3d2f8-1581-4240-aaea-7b4dcaf59147
[ForkJoinPool-1-worker-57] - shipping 80012ffc-e117-45c3-9f45-011af3377ef3
[ForkJoinPool-1-worker-100] - shipping 378672c4-3a4e-4fde-b697-6e8011c32969
[ForkJoinPool-1-worker-115] - shipping 1ca31683-5236-4937-8e56-8ac66bb47725
[ForkJoinPool-1-worker-16] - shipping 026d747b-efc9-4143-9aa7-6cc2d23db4a9
[ForkJoinPool-1-worker-29] - shipping c0684b92-cef5-4a70-9dcc-fe869bca98d9
[ForkJoinPool-1-worker-3] - shipping b64c25f6-79be-4927-b8e4-286da7c65913
[ForkJoinPool-1-worker-31] - shipping 4179666e-7d63-4205-9600-9a7ca8b95f70
[ForkJoinPool-1-worker-45] - shipping a1fa739b-e1a4-41af-ba0e-412b8bade4bd
[ForkJoinPool-1-worker-117] - shipping 096cee8c-0bbd-4305-9d1a-6b6a3981b78c
[ForkJoinPool-1-worker-46] - shipping 9415b49b-4c22-443b-b963-e028b2ed7ee7
[ForkJoinPool-1-worker-73] - shipping 883c1c04-655c-4c81-9fca-5c2ead73d020
[ForkJoinPool-1-worker-59] - shipping 0cb6a7b3-de0b-416f-95b5-96c3c36eff2b
[ForkJoinPool-1-worker-114] - shipping c933b877-cce8-4a1b-94dc-fd60caeac588
[ForkJoinPool-1-worker-103] - shipping 1a7e37da-bc04-4e3c-883f-9ac07d52a2e1
[ForkJoinPool-1-worker-87] - shipping b54933d9-9b37-449f-a9d3-7035291bb921
[ForkJoinPool-1-worker-43] - shipping 2c464f2b-361e-4d41-9c28-07c4d388d40f
[ForkJoinPool-1-worker-17] - shipping 2251626e-238a-4c97-9dad-00e8439c771b
[ForkJoinPool-1-worker-74] - shipping 58bf4b6c-b24d-4d7c-a419-f99082cf1e38
[ForkJoinPool-1-worker-101] - shipping d81a42f3-3a13-4081-8954-0f745c90ce38
[ForkJoinPool-1-worker-60] - shipping bbcf6191-294a-4967-b071-3b35d24281c9
[ForkJoinPool-1-worker-86] - shipping e4855f87-4115-4402-847b-c05fed548cab
[ForkJoinPool-1-worker-102] - shipping 681e63e8-518d-4c1f-88ae-4c5f3c2bdbb8
[ForkJoinPool-1-worker-32] - shipping 1045d559-9c9a-46f3-9669-6a434c6a2e3e
[ForkJoinPool-1-worker-88] - shipping 1cd3c021-2f2b-44d4-ad16-5f98eb61eda6
[ForkJoinPool-1-worker-2] - shipping b2f1b58c-01b0-4630-af11-415705706044
[ForkJoinPool-1-worker-58] - shipping 6741c584-da8c-48b6-a7fe-d595ecaf9c8a
[ForkJoinPool-1-worker-29] - shipping c5fae8fd-dca1-4c74-8490-6e780708dbe6
[ForkJoinPool-1-worker-44] - shipping 0886c29c-921c-4cf7-b5dd-21a29e35981d
[ForkJoinPool-1-worker-16] - shipping 6b0be64f-8593-42a3-9ff8-99f5946290b4
[ForkJoinPool-1-worker-100] - shipping 44b06796-fa03-4d94-ac2a-614d95aeee6a
[ForkJoinPool-1-worker-57] - shipping c0782db1-cec2-426b-a903-11f0e3d0a5a6
[ForkJoinPool-1-worker-116] - shipping 86a3a7c0-5962-415a-8c26-fd885c8e9b54
[ForkJoinPool-1-worker-115] - shipping 41308bc1-85bb-4b4e-accb-1e61f5ca555e
[ForkJoinPool-1-worker-30] - shipping 1576d47c-6963-45b6-b8ca-30a084015bb9
[ForkJoinPool-1-worker-1] - shipping 55228e3c-19a9-4970-bc8a-cfb0b4551175
[ForkJoinPool-1-worker-15] - shipping e0290bf4-9912-4214-b7ad-06fa9461b347
[ForkJoinPool-1-worker-72] - shipping 8a4b732e-6c53-403b-9e50-062a878d5f97
[ForkJoinPool-1-worker-31] - shipping c19b7fe6-dc1e-4d12-af53-c20e198eaf1f
[ForkJoinPool-1-worker-3] - shipping 79397dbd-45d8-46fe-994d-d0a566ec2992
[ForkJoinPool-1-worker-45] - shipping d63f3aeb-6775-43ca-b9e9-1d7e5ec6f4b6
[ForkJoinPool-1-worker-117] - shipping e604526e-81e4-4933-a2bf-1b7453172631
[ForkJoinPool-1-worker-73] - shipping f700e7ff-1b0b-4eb7-8a62-de138142fd8a
[ForkJoinPool-1-worker-46] - shipping 90d98e49-705f-4c2e-9d4e-821727b6e253
[ForkJoinPool-1-worker-59] - shipping b073a728-1ebd-4b12-9331-7c60d6e4db2c
[ForkJoinPool-1-worker-114] - shipping 2f2dc5d0-af1e-45a3-bfa5-f419630f6908
[ForkJoinPool-1-worker-87] - shipping 1a8619a9-7680-49fa-95b0-593b4a55f945
[ForkJoinPool-1-worker-103] - shipping 1bb07be4-8ca8-4d4c-9226-7ed8d6fc9109
[ForkJoinPool-1-worker-74] - shipping c9408dad-e47d-4c46-987b-7a32240890b5
[ForkJoinPool-1-worker-60] - shipping f4c8183f-69e9-4a54-9466-a164aafcd382
[ForkJoinPool-1-worker-88] - shipping 8dff01bc-cf83-4d2c-9efb-fe89bc568f18
[ForkJoinPool-1-worker-101] - shipping dc33475e-d3e5-45d0-b4ed-4cb2635ccb8f
[ForkJoinPool-1-worker-43] - shipping cdbfff6d-7bc8-4dbe-903b-911c27d2ac1b
[ForkJoinPool-1-worker-17] - shipping d3abbecf-cd45-4147-b0d7-20adfa8d6f40
[ForkJoinPool-1-worker-32] - shipping df209f65-9413-4dab-806e-ae95e4f7bb59
[ForkJoinPool-1-worker-86] - shipping 0b3ae86d-b209-4020-bb11-9229edc6a087
[ForkJoinPool-1-worker-102] - shipping ce54dcc6-e1de-495c-a365-b38a6985b30c
[ForkJoinPool-1-worker-2] - shipping 34902cc5-ebf1-4c62-a6e9-b514735fd8f5
[ForkJoinPool-1-worker-44] - shipping 4113d741-9508-421b-bf08-f6bcba7259e5
[ForkJoinPool-1-worker-58] - shipping aa26cb59-d027-4ef5-b52e-98ec5468e476
[ForkJoinPool-1-worker-29] - shipping f7f6f7ed-75b8-4997-9896-bd0db0395a67
[ForkJoinPool-1-worker-16] - shipping 69544d73-6e95-4c79-9377-fb7882044e5b
[ForkJoinPool-1-worker-115] - shipping 9cc78318-c958-4b5e-94cb-2206153c5a0f
[ForkJoinPool-1-worker-72] - shipping b09d18ae-5b9c-4d8d-ad98-91379c97eb85
[ForkJoinPool-1-worker-15] - shipping d38f29e6-ee78-46ac-bde6-0548def21939
[ForkJoinPool-1-worker-1] - shipping 73dc7f66-8360-4428-879b-4726a2e8b8a6
[ForkJoinPool-1-worker-30] - shipping e8347e09-9981-45f4-a694-da83d9871f5e
[ForkJoinPool-1-worker-57] - shipping 84ecad79-3c67-4366-906e-387f48584549
[ForkJoinPool-1-worker-116] - shipping e4c3da3b-be23-4c6e-8356-996deb68fa98
[ForkJoinPool-1-worker-100] - shipping a19d22f6-9461-4437-a3a3-eef3f89b646d
[ForkJoinPool-1-worker-31] - shipping 5f067b70-314f-47e9-8d18-b90e344ecd0d
[ForkJoinPool-1-worker-45] - shipping ee6372c9-eaec-49ca-b9df-fcc52805e3c7
[ForkJoinPool-1-worker-3] - shipping 76894f37-587a-41b4-bb79-075f30caf456
[ForkJoinPool-1-worker-59] - shipping 585673dd-6aba-4a96-a6a7-76eedec16691
[ForkJoinPool-1-worker-73] - shipping 681b64b4-7990-42aa-a2a4-50af3a89f55f
[ForkJoinPool-1-worker-117] - shipping 3230cd9a-e214-4f46-b896-9dd0b208da25
[ForkJoinPool-1-worker-46] - shipping 179a7af9-bdca-40db-8122-c6fde8bea6fd
[ForkJoinPool-1-worker-114] - shipping db0ca909-5b00-406a-a1a3-8f7454c41175
[ForkJoinPool-1-worker-87] - shipping 56a9f230-213e-40b2-b9cc-a83376a56049
[ForkJoinPool-1-worker-103] - shipping cd976237-d071-46c1-9dd9-c4bf4965fd97
[ForkJoinPool-1-worker-74] - shipping 11b54f94-89ce-4421-aa05-d52aa702a599
[ForkJoinPool-1-worker-86] - shipping f76ea601-c445-4744-8c9f-0c5b6f660de4
[ForkJoinPool-1-worker-32] - shipping 8f017407-28e1-4170-8da4-96ae08434580
[ForkJoinPool-1-worker-43] - shipping 8cc39db8-7337-48ee-9ec3-d5fabe6785f9
[ForkJoinPool-1-worker-17] - shipping 01bb360e-531a-4807-8426-8dcf85c14756
[ForkJoinPool-1-worker-101] - shipping 175bfe53-131a-41f5-a665-4c5609e5ac04
[ForkJoinPool-1-worker-88] - shipping 07713280-c8f7-4cd0-8152-656f95d68aec
[ForkJoinPool-1-worker-60] - shipping 7bdb9554-03ff-49f8-a9b8-f2ec478b5dec
[ForkJoinPool-1-worker-102] - shipping 610ac6fc-116d-495a-873c-fe22bda8bc5a
[ForkJoinPool-1-worker-116] - shipping d779d6a0-bccb-4aab-a90c-f7ee39cf2a0b
[ForkJoinPool-1-worker-3] - shipping dca265ff-e048-41a4-96e6-446a6d5f4046
[ForkJoinPool-1-worker-32] - shipping 4de789cc-f9fb-4f66-b1f3-172cf787fd9c
[ForkJoinPool-1-worker-74] - shipping 0a3a3c3a-d705-478c-92b5-dd3cee9e45d7
#[run-main-0] - executor pool is shutdown
[success] Total time: 5 s, completed Apr 1, 2018 6:59:38 PM
```