- [x] добавить параллельный запуск algorythmAnnealing + algorythmGreedy
- [x] в аннилинг гриди алгоритме можно добавить развлекалку, когда скоре уменьшается оно может об этом писать, тогда будет понятно как идет процесс + если скоре уже 0 то можно завершать процедуру
- [x] ~~make tiles fly to their positions in video~~
- [x] performance measure
- [ ] make UI
- [x] make progress bar
- [x] выводить промежуточные результаты на форму
- [x] ~~создать второго витнеса или расширить предыдущего~~
- [x] выводить температуру каждый раз когда улучшается решение, сделать температуру зависимой от текущего счета. Посмотреть, улучшилось ли время нахождения решения
- [x] ~~оптимизировать температуру в зависимости от текущего решения~~
- [x] find nice color palette
- [x] UI design
- [x] построить график переходов при поиске решения, т.е. таблицу вида:
первоначальное решение        следующее решение       сколько переходов      сколько непереходов
7  -> 3      1357       23550   эта строка означает что у нас было 1357 успешных попыток перехода от решения со счетом 7 к решению со счетом 3 и 23550 неудачных попыток, ну если мы переходим от 7 к 3 то у нас все попытки будут удачными, но если бы от 3 к 7 то не все
- [x] сделать оптимизацию вычисления функции интерсект, потому что на нее уходит основное время
- [x] отменить джамп и посмотреть стало ли хуже
- [x] ~~якщо рішення надовго зависло в деякій позиції то робити smash()~~
- [x] доработать интерфейс wintess на предмет взаимодействия с экраном
- [ ] придумать несколько задач на оптимизацию и реализовать их
- [x] объединить интерфейсы witness + markovChain 
- [x] fix issues ^
- [ ] если в скоре учитывать насколько близко расположены незакрытые квадраты и квадраты с наложением то это может дать еще немного улучшения, потому что рядом расположенные незакрытые квадраты закрыть легче
- [ ] bloom filter: allows effectively saving codes
- [x] сохранить текущую копию в отдельном месте для использования частей в будущем
- [x] заменить имя класса SolutionHybrid на Tetris
- [x] заменить тип score с int на double (в том числе в форматных строках)
- [x] убрать статистику
- [X] добавить константам префикс TETRIS
- [X] удалить все ненужные классы
- [X] переименовать PNGMaker в TetrisPNG
- [x] переименовать make в saveArea
- [x] удалить ненужные константы
- [ ] добавить в TetrisPNG функцию saveChart для сохранения графика (использовать библиотеку org.jfree.chart)
- [ ] добавить график в UI
- [ ] добавить в UI текстовую надпись для сообщения текущего счета
- [x] перенести Tile.init(); из App в класс Tile
- [x] перенести fire в Utils
- [x] убрать класс Annealing, оставить только интерфейс MarkovChain
- [x] переименовать afterNewSolution -> afterBetterSolutionFound
- [x] переименовать MarkovChain в Chainable
- [x] удалить colors = Utils.getPalette(((Solution)s).tiles.length); из afterStart, vj;yj или даже всю реализацию, по дефолту все и так хорошо
- [x] удалить getWitness()
- [x] победить ошибки при параллельной записи изображений
- [x] перенести рандом в утілс

- [x] multithreading
- [x] create video
- [x] ~~make babyFeeder as parametr~~
- [x] split train into play() and train()
- [x] Markdown syntaxxx look 
- [x] ~~add values to explain()~~
- [x] ~~speed up with CUDA (nvidia)~~
- [x] ~~make treesearch algorithm visualisation~~
- [x] ~~навчання через покоління ~~
- [x] ~~optimize neuronet shape ~~
- [x] use simulated annealing
- [x] подобрать оптимальную температуру
- [x] annealing video flashing
- [X] ~~make competition between algorythmAnnealing and algorythmGreedy~~
- [x] написать алгоритм рандомного заполнения, запустить несколько раз, найти средний рандомный score чтоб было на что опираться при оценке хорошо у нас получилось или плохо
- [x] запустить N рандомных заполнений на одной и той же игре и выбрать лучший, опять таки с той же целью
- [x] ~~для одной и той же игры получить 3 оценки, лучший из N случайных заполнений, отжиг с помощью перебора N вариантов и MCTS и тут надо как то найти аналог N, чтоб сравнивать подобное с подобным, может просто подбирать N чтоб первые 2 алгоритма занимали такое же время~~
- [x] реализовать жадный алгоритм заполнения, жадных алгоритмов может быть тоже много, можно выбирать рандомную последовательность тайлов а потом или жадно заполнять
- [x] сделать константы TETRIS_TILES_COUNT и ACTIONS_COUNT вычисляемыми
- [x] исправить алгоритм generateTiles()
- [x] запускать annealing algorythm в цикле с разной начальной температурой
- [x] сделать гибрид algorythmGreedy и algorythmAnnealing
- [x] шаблон проектирования witness
- [x] сделать algorythmAnnealing независимым от решаемой задачи с помощью Dependency Injection (DI)

- [ ] try maxFlow algorythm
- [ ] 3 SAT задача 
- [ ] следующий проект может быть по opencv там как раз точно понадобятся нейросети это распознавание лиц на изображениях, и параметров хюмана и вообще работа с изображениями
- [ ] перенести части AlphaZero с нейросетями в другой проект
- [ ] genetic algorythm
